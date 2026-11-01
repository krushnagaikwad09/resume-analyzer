package com.krushnag.pharmacy.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class ApiModels {

    private ApiModels() {
    }

    public record CreateMedicineRequest(
            @NotBlank String sku,
            @NotBlank String name,
            String description) {
    }

    public record CreateBatchRequest(
            @NotBlank String batchNumber,
            @NotNull LocalDate expiryDate,
            @Positive int quantityReceived) {
    }

    public record SellRequest(@Positive int quantity) {
    }

    public record MedicineResponse(Long id, String sku, String name, String description) {
    }

    public record BatchResponse(
            Long id,
            Long medicineId,
            String batchNumber,
            LocalDate expiryDate,
            int quantityReceived,
            int quantityAvailable) {
    }

    public record SaleAllocation(
            Long saleId,
            Long batchId,
            String batchNumber,
            LocalDate expiryDate,
            int quantity) {
    }

    public record SaleResponse(
            Long medicineId,
            int quantitySold,
            LocalDateTime soldAt,
            java.util.List<SaleAllocation> allocations) {
    }

    public record ErrorResponse(String error, String message) {
    }
}
