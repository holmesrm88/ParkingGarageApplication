package org.holmesrm8.PG.service.impl;

import org.holmesrm8.PG.model.CsvUpload;
import org.holmesrm8.PG.model.ParkingTransactions;
import org.holmesrm8.PG.model.Vehicle;
import org.holmesrm8.PG.repository.ParkingTransactionRepository;
import org.holmesrm8.PG.repository.VehicleRepository;
import org.holmesrm8.PG.service.Impl.GarageServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.Assert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GarageServiceImplTest {

    @InjectMocks
    private GarageServiceImpl garageService;

    private List<CsvUpload> csvUploads;
    private List<Vehicle> vehicles;
    private List<ParkingTransactions> parkingTransactions;

    @Mock
    VehicleRepository vehicleRepository;

    @Mock
    ParkingTransactionRepository parkingTransactionRepository;



    @Before
    public void setup(){
        MockitoAnnotations.openMocks(this);

        garageService = new GarageServiceImpl(vehicleRepository, parkingTransactionRepository);

        csvUploads = new ArrayList<>();
        vehicles = new ArrayList<>();
        parkingTransactions = new ArrayList<>();

        CsvUpload c1 = buildCsvUploadObject("Vehicle1", "I", "2018-11-01T08:00:00", true, new BigDecimal(0), 0.00);
        CsvUpload c2 = buildCsvUploadObject("Vehicle1", "O", "2018-11-01T10:15:00", true, new BigDecimal(0), 0.00);
        CsvUpload c3 = buildCsvUploadObject("Vehicle2", "I", "2018-11-10T08:00:00", false, new BigDecimal(30), 0.00);
        CsvUpload c4 = buildCsvUploadObject("Vehicle2", "O", "2018-11-10T09:59:00", false, new BigDecimal(30), 0.00);
        CsvUpload c5 = buildCsvUploadObject("Vehicle3", "I", "2018-11-27T08:00:00", false, new BigDecimal(0), 15.5);
        CsvUpload c6 = buildCsvUploadObject("Vehicle3", "O", "2018-11-27T08:30:00", false, new BigDecimal(0), 15.5);
        csvUploads.add(c1);
        csvUploads.add(c2);
        csvUploads.add(c3);
        csvUploads.add(c4);
        csvUploads.add(c5);
        csvUploads.add(c6);

        Vehicle v1 = buildVehicleObject("Vehicle1", "I", "2018-11-01T08:00:00", true, new BigDecimal(0), new BigDecimal(0.00));
        Vehicle v2 = buildVehicleObject("Vehicle1", "O", "2018-11-01T10:15:00", true, new BigDecimal(0), new BigDecimal(0.00));
        Vehicle v3 = buildVehicleObject("Vehicle2", "I", "2018-11-10T08:00:00", false, new BigDecimal(30), new BigDecimal(0.00));
        Vehicle v4 = buildVehicleObject("Vehicle2", "O", "2018-11-10T09:59:00", false, new BigDecimal(30), new BigDecimal(0.00));
        Vehicle v5 = buildVehicleObject("Vehicle3", "I", "2018-11-27T08:00:00", false, new BigDecimal(0), new BigDecimal(15.5));
        Vehicle v6 = buildVehicleObject("Vehicle3", "O", "2018-11-27T08:30:00", false, new BigDecimal(0), new BigDecimal(15.5));
        vehicles.add(v1);
        vehicles.add(v2);
        vehicles.add(v3);
        vehicles.add(v4);
        vehicles.add(v5);
        vehicles.add(v6);

        ParkingTransactions p1 = buildParkingTransactionObject(new BigDecimal(0.0), "Vehicle1", "O", "2018-11-01T10:15:00");
        ParkingTransactions p2 = buildParkingTransactionObject(new BigDecimal(0.0), "Vehicle2", "O", "2018-11-10T09:59:00");
        ParkingTransactions p3 = buildParkingTransactionObject(new BigDecimal(2.535), "Vehicle3", "O", "2018-11-27T08:30:00");
        parkingTransactions.add(p1);
        parkingTransactions.add(p2);
        parkingTransactions.add(p3);
    }

    @Test
    public void testCalculateOutput_success() throws IOException, ParseException {
        when(vehicleRepository.saveAll(anyList())).thenReturn(vehicles);
        when(vehicleRepository.getUniqueLicensePlates()).thenReturn(buildUniquePlates());
        when(vehicleRepository.findAll()).thenReturn(vehicles);
        when(parkingTransactionRepository.findAll()).thenReturn(parkingTransactions);
        BigDecimal outcome = garageService.saveAllUploads(csvUploads);
        Assert.assertEquals(new BigDecimal(32.54).setScale(2, RoundingMode.CEILING), outcome);
    }

    @Test
    public void testCalculateOutput_successLongerThanADay() throws IOException, ParseException{
        vehicles.get(5).setTime("2018-11-29T08:30:00");
        parkingTransactions.get(2).setTransaction(new BigDecimal(36.34));

        when(vehicleRepository.saveAll(anyList())).thenReturn(vehicles);
        when(vehicleRepository.getUniqueLicensePlates()).thenReturn(buildUniquePlates());
        when(vehicleRepository.findAll()).thenReturn(vehicles);
        when(parkingTransactionRepository.findAll()).thenReturn(parkingTransactions);
        BigDecimal outcome = garageService.saveAllUploads(csvUploads);
        Assert.assertEquals(new BigDecimal(66.34).setScale(2, RoundingMode.CEILING), outcome);
    }

    @Test
    public void testCalculateOutput_successMultipleDays() throws IOException, ParseException{
        Vehicle v7 = buildVehicleObject("Vehicle3", "I", "2018-11-10T08:00:00", false, new BigDecimal(0), new BigDecimal(15.5));
        Vehicle v8 = buildVehicleObject("Vehicle3", "O", "2018-11-10T09:59:00", false, new BigDecimal(0), new BigDecimal(15.5));
        vehicles.add(v7);
        vehicles.add(v8);

        ParkingTransactions p4 = buildParkingTransactionObject(new BigDecimal(5.07), "Vehicle3", "O", "2018-11-10T09:59:00");
        parkingTransactions.add(p4);



        when(vehicleRepository.saveAll(anyList())).thenReturn(vehicles);
        when(vehicleRepository.getUniqueLicensePlates()).thenReturn(buildUniquePlates());
        when(vehicleRepository.findAll()).thenReturn(vehicles);
        when(parkingTransactionRepository.findAll()).thenReturn(parkingTransactions);
        BigDecimal outcome = garageService.saveAllUploads(csvUploads);
        Assert.assertEquals(new BigDecimal(37.61).setScale(2, RoundingMode.CEILING), outcome);

    }

    public CsvUpload buildCsvUploadObject(String licensePlate, String direction, String time, boolean parkingPass, BigDecimal prepaid, Double discount){
        CsvUpload csvUpload = new CsvUpload();
        csvUpload.setLicensePlate(licensePlate);
        csvUpload.setDirection(direction);
        csvUpload.setTime(time);
        csvUpload.setParkingPass(parkingPass);
        csvUpload.setPrePaidAmount(prepaid.doubleValue());
        csvUpload.setDiscount(discount);
        return csvUpload;
    }

    public Vehicle buildVehicleObject(String licensePlate, String direction, String time, boolean parkingPass, BigDecimal prepaid, BigDecimal discount){
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(licensePlate);
        vehicle.setDirection(direction);
        vehicle.setTime(time);
        vehicle.setParkingPass(parkingPass);
        vehicle.setPrePaidAmount(prepaid);
        vehicle.setDiscount(discount);
        return vehicle;
    }

    private List<String> buildUniquePlates(){
        List<String> uniquePlates = new ArrayList<>();
        uniquePlates.add("Vehicle1");
        uniquePlates.add("Vehicle2");
        uniquePlates.add("Vehicle3");
        return uniquePlates;
    }

    private ParkingTransactions buildParkingTransactionObject(BigDecimal transaction, String licensePlate, String direction, String departureTime){
        ParkingTransactions pt = new ParkingTransactions();
        pt.setTransaction(transaction);
        pt.setLicensePlate(licensePlate);
        pt.setDepartureTime(departureTime);
        pt.setDirection(direction);
        return pt;
    }


}
