package com.krushnag.pharmacy.service;

import com.krushnag.pharmacy.api.ApiModels.BatchResponse;
import com.krushnag.pharmacy.api.ApiModels.CreateBatchRequest;
import com.krushnag.pharmacy.api.ApiModels.CreateMedicineRequest;
import com.krushnag.pharmacy.api.ApiModels.MedicineResponse;
import com.krushnag.pharmacy.api.ApiModels.SaleAllocation;
import com.krushnag.pharmacy.api.ApiModels.SaleResponse;
import com.krushnag.pharmacy.model.Batch;
import com.krushnag.pharmacy.model.Medicine;
import com.krushnag.pharmacy.model.Sale;
import com.krushnag.pharmacy.repository.BatchRepository;
import com.krushnag.pharmacy.repository.MedicineRepository;
import com.krushnag.pharmacy.repository.SaleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PharmacyInventoryService {

    private final MedicineRepository medicineRepository;
    private final BatchRepository batchRepository;
    private final SaleRepository saleRepository;

    public PharmacyInventoryService(
            MedicineRepository medicineRepository,
            BatchRepository batchRepository,
            SaleRepository saleRepository) {
        this.medicineRepository = medicineRepository;
        this.batchRepository = batchRepository;
        this.saleRepository = saleRepository;
    }

    @Transactional
    public MedicineResponse createMedicine(CreateMedicineRequest request) {
        try {
            Medicine medicine = medicineRepository.save(
                    new Medicine(request.sku().trim(), request.name().trim(), request.description()));
            return toMedicineResponse(medicine);
        } catch (DataIntegrityViolationException exception) {
            throw new IllegalArgumentException("A medicine with this SKU already exists.", exception);
        }
    }

    @Transactional(readOnly = true)
    public List<MedicineResponse> getMedicines() {
        return medicineRepository.findAll().stream().map(this::toMedicineResponse).toList();
    }

    @Transactional
    public BatchResponse addBatch(Long medicineId, CreateBatchRequest request) {
        Medicine medicine = getMedicineOrThrow(medicineId);
        Batch batch = batchRepository.save(new Batch(
                medicine, request.batchNumber().trim(), request.expiryDate(), request.quantityReceived()));
        return toBatchResponse(batch);
    }

    @Transactional(readOnly = true)
    public List<BatchResponse> getBatches(Long medicineId) {
        getMedicineOrThrow(medicineId);
        return batchRepository.findByMedicineIdOrderByExpiryDateAsc(medicineId)
                .stream().map(this::toBatchResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<BatchResponse> getExpiringBatches(int days) {
        LocalDate today = LocalDate.now();
        return batchRepository.findByExpiryDateBetweenAndQuantityAvailableGreaterThanOrderByExpiryDateAsc(
                        today.plusDays(1), today.plusDays(days), 0)
                .stream().map(this::toBatchResponse).toList();
    }

    @Transactional
    public SaleResponse sell(Long medicineId, int requestedQuantity) {
        getMedicineOrThrow(medicineId);
        LocalDate today = LocalDate.now();
        List<Batch> batches = batchRepository.findSellableBatchesInFefoOrder(medicineId, today);
        int availableQuantity = batches.stream().mapToInt(Batch::getQuantityAvailable).sum();
        if (availableQuantity < requestedQuantity) {
            throw new InsufficientStockException(
                    "Requested " + requestedQuantity + " units but only " + availableQuantity + " are available.");
        }

        int remaining = requestedQuantity;
        LocalDateTime soldAt = LocalDateTime.now();
        List<Sale> sales = new ArrayList<>();
        for (Batch batch : batches) {
            if (remaining == 0) {
                break;
            }
            int quantityFromBatch = Math.min(remaining, batch.getQuantityAvailable());
            batch.removeStock(quantityFromBatch);
            sales.add(new Sale(batch, quantityFromBatch, soldAt));
            remaining -= quantityFromBatch;
        }

        List<SaleAllocation> allocations = saleRepository.saveAll(sales).stream()
                .map(sale -> new SaleAllocation(
                        sale.getId(),
                        sale.getBatch().getId(),
                        sale.getBatch().getBatchNumber(),
                        sale.getBatch().getExpiryDate(),
                        sale.getQuantity()))
                .toList();
        return new SaleResponse(medicineId, requestedQuantity, soldAt, allocations);
    }

    private Medicine getMedicineOrThrow(Long medicineId) {
        return medicineRepository.findById(medicineId)
                .orElseThrow(() -> new EntityNotFoundException("Medicine " + medicineId + " was not found."));
    }

    private MedicineResponse toMedicineResponse(Medicine medicine) {
        return new MedicineResponse(
                medicine.getId(), medicine.getSku(), medicine.getName(), medicine.getDescription());
    }

    private BatchResponse toBatchResponse(Batch batch) {
        return new BatchResponse(
                batch.getId(),
                batch.getMedicine().getId(),
                batch.getBatchNumber(),
                batch.getExpiryDate(),
                batch.getQuantityReceived(),
                batch.getQuantityAvailable());
    }
}
