package com.krushnag.pharmacy;

import com.krushnag.pharmacy.api.ApiModels.CreateBatchRequest;
import com.krushnag.pharmacy.api.ApiModels.CreateMedicineRequest;
import com.krushnag.pharmacy.api.ApiModels.SaleResponse;
import com.krushnag.pharmacy.model.Batch;
import com.krushnag.pharmacy.model.Medicine;
import com.krushnag.pharmacy.repository.BatchRepository;
import com.krushnag.pharmacy.repository.MedicineRepository;
import com.krushnag.pharmacy.repository.SaleRepository;
import com.krushnag.pharmacy.service.InsufficientStockException;
import com.krushnag.pharmacy.service.PharmacyInventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class PharmacyInventoryServiceTests {

    @Autowired
    private PharmacyInventoryService inventoryService;

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private SaleRepository saleRepository;

    private Long medicineId;

    @BeforeEach
    void setUp() {
        saleRepository.deleteAll();
        batchRepository.deleteAll();
        medicineRepository.deleteAll();
        medicineId = inventoryService.createMedicine(
                new CreateMedicineRequest("SKU-001", "Amoxicillin", "Capsules")).id();
    }

    @Test
    void sellsSoonestExpiringStockFirstAcrossBatches() {
        LocalDate today = LocalDate.now();
        inventoryService.addBatch(medicineId, new CreateBatchRequest("LATER", today.plusDays(20), 5));
        inventoryService.addBatch(medicineId, new CreateBatchRequest("SOONER", today.plusDays(5), 2));
        inventoryService.addBatch(medicineId, new CreateBatchRequest("EXPIRED", today.minusDays(1), 10));

        SaleResponse response = inventoryService.sell(medicineId, 4);

        assertThat(response.allocations())
                .extracting(allocation -> allocation.batchNumber())
                .containsExactly("SOONER", "LATER");
        assertThat(response.allocations())
                .extracting(allocation -> allocation.quantity())
                .containsExactly(2, 2);
        assertThat(batchRepository.findByMedicineIdOrderByExpiryDateAsc(medicineId))
                .filteredOn(batch -> batch.getBatchNumber().equals("EXPIRED"))
                .extracting(Batch::getQuantityAvailable)
                .containsExactly(10);
        assertThat(saleRepository.count()).isEqualTo(2);
    }

    @Test
    void insufficientStockDoesNotCreateSalesOrChangeQuantities() {
        inventoryService.addBatch(medicineId, new CreateBatchRequest("ONLY", LocalDate.now().plusDays(10), 2));

        assertThatThrownBy(() -> inventoryService.sell(medicineId, 3))
                .isInstanceOf(InsufficientStockException.class);

        assertThat(saleRepository.count()).isZero();
        assertThat(batchRepository.findByMedicineIdOrderByExpiryDateAsc(medicineId))
                .extracting(Batch::getQuantityAvailable)
                .containsExactly(2);
    }

    @Test
    void expiringBatchesCanBeFilteredWithinFifteenToThirtyDays() {
        LocalDate today = LocalDate.now();
        inventoryService.addBatch(medicineId, new CreateBatchRequest("SOON", today.plusDays(15), 4));
        inventoryService.addBatch(medicineId, new CreateBatchRequest("LATER", today.plusDays(31), 4));
        Medicine medicine = medicineRepository.findById(medicineId).orElseThrow();
        batchRepository.save(new Batch(medicine, "EXPIRED", today.minusDays(1), 4));

        assertThat(inventoryService.getExpiringBatches(30))
                .extracting(batch -> batch.batchNumber())
                .containsExactly("SOON");
    }
}
