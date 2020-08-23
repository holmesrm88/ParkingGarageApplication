package org.holmesrm8.PG.service.Impl;

import lombok.extern.java.Log;
import org.holmesrm8.PG.model.*;
import org.holmesrm8.PG.repository.ParkingTransactionRepository;
import org.holmesrm8.PG.repository.VehicleRepository;
import org.holmesrm8.PG.service.GarageService;

import org.holmesrm8.PG.util.ParkingGarageConstants;
import org.holmesrm8.PG.util.converter.CsvUploadToVehicleConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
@Log
public class GarageServiceImpl implements GarageService {
    private VehicleRepository vehicleRepository;
    private ParkingTransactionRepository parkingTransactionRepository;

    @Autowired
    public GarageServiceImpl(VehicleRepository vehicleRepository, ParkingTransactionRepository parkingTransactionRepository){
        this.vehicleRepository = vehicleRepository;
        this.parkingTransactionRepository = parkingTransactionRepository;
    }

    @Override
    public BigDecimal saveAllUploads(List<CsvUpload> csvUploads) throws ParseException, IOException {
        log.info("GarageServiceImpl | saveAllUploads | START");
        CsvUploadToVehicleConverter csvUploadToVehicleConverter = new CsvUploadToVehicleConverter();
        // TODO fix converter to use ConversionService
        List<Vehicle> vehicles =  csvUploadToVehicleConverter.convert(csvUploads);
        vehicleRepository.saveAll(vehicles);
        BigDecimal monthlyTotal = calculateOutput();
        log.info("GarageServiceImpl | saveAllUploads | END");
        return  monthlyTotal;
    }

    private BigDecimal calculateOutput() throws ParseException, IOException {
        log.info("GarageServiceImpl | calculateOutput | START");
        FileWriter fileWriter = new FileWriter("ParkingGarageMonthlyOutput.txt");
        PrintWriter printWriter = new PrintWriter(fileWriter);

        List<String> uniquePlates = getDistinctPlates(printWriter);
        List<BigDecimal> prePaidPerCar = getPrePaidPerCar(uniquePlates);


        //Calculate the charges per transaction
        calculateChargesPerTransaction(printWriter);
        getTotalsPerCar(uniquePlates, printWriter);

        // get all transactions for month
        List<ParkingTransactions> monthlyTransactions =  getAllTransactionsForMonth(printWriter);

        //get totals for month
        BigDecimal total = totalForAll(monthlyTransactions, prePaidPerCar, printWriter).setScale(2, RoundingMode.CEILING);

        printWriter.close();
        fileWriter.close();
        clearTableInformation();
        log.info("GarageServiceImpl | calculateOutput | END");
        return total;
    }

    protected List<String> getDistinctPlates(PrintWriter pw){
        log.info("GarageServiceImpl | getDistinctPlates | START");
        List<String> uniquePlates = vehicleRepository.getUniqueLicensePlates();
        log.info("GarageServiceImpl | getDistinctPlates | END");

        return uniquePlates;
    }

    private void calculateChargesPerTransaction(PrintWriter pw) throws ParseException {
        log.info("GarageServiceImpl | calculateChargesPerTransaction | START");

        List<Vehicle> allVehicles = vehicleRepository.findAll();
        List<ParkingTransactions> parkingTransactions = new ArrayList<>();

        for(int i = 0; i < allVehicles.size(); i++){
            if(allVehicles.get(i).getDirection().equals("I")){
                for(int k = i+1; k < allVehicles.size(); k++){
                    if(allVehicles.get(k).getLicensePlate().equals(allVehicles.get(i).getLicensePlate())){
                        ParkingTransactions parkingTransaction = new ParkingTransactions();

                        long minutesBetween = minutesBetween(allVehicles.get(i), allVehicles.get(k));

                        // calculate currentCharge
                        double currentCharge = calculateCharge(minutesBetween, allVehicles.get(k).getParkingPass(), allVehicles.get(k).getDiscount());
                        if(allVehicles.get(k).getPrePaidAmount().doubleValue() > 0){
                            double rateHolder = currentCharge;
                            currentCharge = currentCharge - allVehicles.get(k).getPrePaidAmount().doubleValue() > 0 ? currentCharge - allVehicles.get(k).getPrePaidAmount().doubleValue() : 0;
                            adjustPrePaidAmount(allVehicles, k, rateHolder);
                        }
                        parkingTransaction.setLicensePlate(allVehicles.get(i).getLicensePlate());
                        parkingTransaction.setTransaction(BigDecimal.valueOf(currentCharge));
                        parkingTransaction.setDirection(allVehicles.get(k).getDirection());
                        parkingTransaction.setDepartureTime(allVehicles.get(k).getTime());
                        parkingTransactions.add(parkingTransaction);
                        break;
                    }
                }
            }
        }

        pw.println("---------- INDIVIDUAL CHARGES ------------");
        for(ParkingTransactions p : parkingTransactions){
            pw.println("License plate: " + p.getLicensePlate() + " Charged: $" + p.getTransaction().setScale(2, RoundingMode.CEILING) + " for leaving at " + p.getDepartureTime());
        }
        log.info("GarageServiceImpl | calculateChargesPerTransaction | END");
        parkingTransactionRepository.saveAll(parkingTransactions);
    }

    private double calculateCharge(long minutes, boolean parkingPass, BigDecimal discount){
        log.info("GarageServiceImpl | calculateCharge | START");

        double cost = 0;
        if(!parkingPass){
            while(minutes >= 1440) {
                cost += ParkingGarageConstants.PER_DAY;
                minutes -= 1440;
            }

            if (300 <= minutes && minutes <= 1339){
                cost += ParkingGarageConstants.SIX_TO_FULL_DAY;
            } else if (240 <= minutes && minutes <= 299){
                cost += ParkingGarageConstants.FOUR_TO_FIVE_HOURS;
            } else if (180 <= minutes && minutes <= 239){
                cost += ParkingGarageConstants.THREE_TO_FOUR_HOURS;
            } else if (120 <= minutes && minutes <= 179){
                cost += ParkingGarageConstants.TWO_TO_THREE_HOURS;
            } else if (60 <= minutes && minutes <= 119){
                cost += ParkingGarageConstants.ONE_TO_TWO_HOURS;
            } else if (30 <= minutes && minutes <= 59){
                cost += ParkingGarageConstants.THIRTY_TO_ONE_HOUR;
            } else {
                cost += ParkingGarageConstants.UNDER_30;
            }
            cost = discount.doubleValue() != 0 ? ((100-discount.doubleValue())*cost)/100 : cost;
        }
        log.info("GarageServiceImpl | calculateCharge | END");

        return cost > 0 ? cost : 0;
    }

    private void adjustPrePaidAmount(List<Vehicle> vehicles, int index, double rate){
        log.info("GarageServiceImpl | adjustPrePaidAmount | START");

        String licensePlate = vehicles.get(index).getLicensePlate();
        for(Vehicle v : vehicles){
            if (v.getLicensePlate().equals(licensePlate)){
                v.setPrePaidAmount(BigDecimal.valueOf(v.getPrePaidAmount().doubleValue() - rate > 0 ? v.getPrePaidAmount().doubleValue() - rate - 0 : 0));
            }
        }
        log.info("GarageServiceImpl | adjustPrePaidAmount | END");
    }

    private long minutesBetween(Vehicle v1, Vehicle v2) throws ParseException {
        log.info("GarageServiceImpl | minutesBetween | START");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Instant instant1 = formatter.parse(v1.getTime()).toInstant();
        Instant instant2 = formatter.parse(v2.getTime()).toInstant();

        LocalDateTime ldt1 = LocalDateTime.ofInstant(instant1, ZoneOffset.UTC);
        LocalDateTime ldt2 = LocalDateTime.ofInstant(instant2, ZoneOffset.UTC);

        Duration dur = Duration.between(ldt1, ldt2);
        log.info("GarageServiceImpl | minutesBetween | END");

        return dur.toMinutes();
    }

    private List<ParkingTransactions> getTotalsPerCar(List<String> uniquePlates, PrintWriter pw) throws IOException {
        log.info("GarageServiceImpl | getTotalsPerCar | START");

        List<ParkingTransactions> transactions = parkingTransactionRepository.findAll();

        List<ParkingTransactions> totalCostPerCar = new ArrayList<ParkingTransactions>();
        for(String plate : uniquePlates){
            BigDecimal totalCost = new BigDecimal(0);
            ParkingTransactions pt2 = new ParkingTransactions();
            pt2.setLicensePlate(plate);
            for(ParkingTransactions pt : transactions){
                if(pt.getLicensePlate().equals(plate)){
                    totalCost = totalCost.add(pt.getTransaction().setScale(2, RoundingMode.CEILING));
                }
            }
            pt2.setTransaction(totalCost);
            totalCostPerCar.add(pt2);
        }

        pw.println("---------- TOTAL CHARGES PER CAR ------------");
        for(ParkingTransactions pt : totalCostPerCar){
            pw.println("License Plate " + pt.getLicensePlate() + " Total Charge $" + pt.getTransaction().setScale(2, RoundingMode.CEILING));
        }
        log.info("GarageServiceImpl | getTotalsPerCar | END");

        return totalCostPerCar;
    }

    private List<BigDecimal> getPrePaidPerCar(List<String> uniquePlates){
        log.info("GarageServiceImpl | getPrePaidPerCar | START");

        List<Vehicle> vehicles = vehicleRepository.findAll();

        List<BigDecimal> prepaid = new ArrayList<>();
        for(String plate : uniquePlates){
            for(Vehicle v: vehicles){
                if(v.getLicensePlate().equals(plate)) {
                    prepaid.add(v.getPrePaidAmount());
                    break;
                }
            }
        }
        log.info("GarageServiceImpl | getPrePaidPerCar | END");
        return prepaid;
    }

    private List<ParkingTransactions> getAllTransactionsForMonth(PrintWriter pw){
        pw.println("---------- TOTAL NUMBER OF CARS ------------");
        List<ParkingTransactions> totalNumberOfCarsParked = parkingTransactionRepository.findAll();
        pw.println("Total number of cars parked for the month: " + totalNumberOfCarsParked.size());
        log.info("GarageServiceImpl | getAllTransactionsForMonth");
        return totalNumberOfCarsParked;
    }

    private BigDecimal totalForAll(List<ParkingTransactions> monthlyTransactions, List<BigDecimal> prepaid, PrintWriter pw){
        log.info("GarageServiceImpl | totalForAll | START");

        BigDecimal total = new BigDecimal(0);

        for(BigDecimal v : prepaid){
            total = total.add(v);
        }
        for(ParkingTransactions pt  : monthlyTransactions){
            total = total.add(pt.getTransaction());
        }

        pw.println("---------- TOTAL CHARGES ------------");
        pw.println("TOTAL FOR MONTH: $" +total.setScale(2, RoundingMode.CEILING));

        log.info("GarageServiceImpl | totalForAll | EMD");

        return  total;
    }

    private void clearTableInformation(){
        log.info("GarageServiceImpl | clearTableInformation | START");
        vehicleRepository.deleteAll();
        parkingTransactionRepository.deleteAll();
        log.info("GarageServiceImpl | clearTableInformation | END");

    }
}
