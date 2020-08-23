package org.holmesrm8.PG.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;


@Getter
@Setter
@Entity
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name= "parking_pass")
    private Boolean parkingPass;

    @Column(name = "license_plate", nullable = false)
    private String licensePlate;

    @Column(name = "prepaid")
    private BigDecimal prePaidAmount;

    @Column(name = "discount")
    private BigDecimal discount;

    @Column(name = "direction", nullable = false)
    private String direction;

    @Column(name = "transaction_time", nullable = false)
    private String time;

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", parkingPass=" + parkingPass +
                ", licensePlate='" + licensePlate + '\'' +
                ", prePaidAmount=" + prePaidAmount +
                ", discount=" + discount +
                ", direction='" + direction + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
