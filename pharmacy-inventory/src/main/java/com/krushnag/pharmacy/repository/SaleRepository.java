package com.krushnag.pharmacy.repository;

import com.krushnag.pharmacy.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {
}
