package com.krushnag.pharmacy.repository;

import com.krushnag.pharmacy.model.Batch;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BatchRepository extends JpaRepository<Batch, Long> {

    List<Batch> findByMedicineIdOrderByExpiryDateAsc(Long medicineId);

    List<Batch> findByExpiryDateBetweenAndQuantityAvailableGreaterThanOrderByExpiryDateAsc(
            LocalDate startDate, LocalDate endDate, int minimumQuantity);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select b from Batch b
            where b.medicine.id = :medicineId
              and b.expiryDate > :today
              and b.quantityAvailable > 0
            order by b.expiryDate asc, b.id asc
            """)
    List<Batch> findSellableBatchesInFefoOrder(
            @Param("medicineId") Long medicineId, @Param("today") LocalDate today);
}
