package org.holmesrm8.PG.util.converter;

import org.holmesrm8.PG.model.CsvUpload;
import org.holmesrm8.PG.model.Vehicle;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CsvUploadToVehicleConverter {

    public List<Vehicle> convert(List<CsvUpload> csvUpload) {
        List<Vehicle> vehicles = new ArrayList<>();

        for(CsvUpload c : csvUpload){
            Vehicle v = new Vehicle();
            v.setDirection(c.getDirection());
            v.setDiscount(BigDecimal.valueOf(c.getDiscount()));
            v.setLicensePlate(c.getLicensePlate());
            v.setParkingPass(c.getParkingPass());
            v.setPrePaidAmount(BigDecimal.valueOf(c.getPrePaidAmount()));
            v.setTime(c.getTime());

            vehicles.add(v);
        }
        return vehicles;
    }
}
