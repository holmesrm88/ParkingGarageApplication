package org.holmesrm8.PG.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "parking_transactions")
public class ParkingTransactions {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "transaction_amount", nullable = false)
    private BigDecimal transaction;

    @Column(name = "license_plate", nullable = false)
    private String licensePlate;

    @Column(name = "direction", nullable = false)
    private String direction;

    @Column(name= "departure_time", nullable = false)
    private String departureTime;

    @Override
    public String toString() {
        return "ParkingTransactions{" +
                "id=" + id +
                ", transaction=" + transaction +
                ", licensePlate='" + licensePlate + '\'' +
                ", direction='" + direction + '\'' +
                ", departureTime='" + departureTime + '\'' +
                '}';
    }
}
