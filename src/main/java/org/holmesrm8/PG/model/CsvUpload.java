package org.holmesrm8.PG.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CsvUpload {

    @CsvBindByName(column= "Parking Pass")
    private Boolean parkingPass = false;

    @CsvBindByName(column = "License Number", required = true)
    private String licensePlate;

    @CsvBindByName(column = "Prepaid")
    private double prePaidAmount = 0.00;

    @CsvBindByName(column = "Discount")
    private Double discount = 0.00;

    @CsvBindByName(column = "Direction", required = true)
    private String direction;

    @CsvBindByName(column = "Time", required = true)
    private String time;

    @Override
    public String toString() {
        return "CsvUpload{" +
                "parkingPass=" + parkingPass +
                ", licensePlate='" + licensePlate + '\'' +
                ", prePaidAmount=" + prePaidAmount +
                ", discount=" + discount +
                ", direction='" + direction + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
