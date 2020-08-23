package org.holmesrm8.PG.repository;

import org.holmesrm8.PG.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    @Query(value = "SELECT DISTINCT license_plate FROM VEHICLE", nativeQuery = true)
    List<String> getUniqueLicensePlates();
}
