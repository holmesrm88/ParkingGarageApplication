package org.holmesrm8.PG.repository;

import org.holmesrm8.PG.model.ParkingTransactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingTransactionRepository extends JpaRepository<ParkingTransactions, Long> {
}
