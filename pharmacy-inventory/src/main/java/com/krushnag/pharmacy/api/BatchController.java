package com.krushnag.pharmacy.api;

import com.krushnag.pharmacy.api.ApiModels.BatchResponse;
import com.krushnag.pharmacy.api.ApiModels.CreateBatchRequest;
import com.krushnag.pharmacy.service.PharmacyInventoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api")
public class BatchController {

    private final PharmacyInventoryService inventoryService;

    public BatchController(PharmacyInventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/medicines/{medicineId}/batches")
    @ResponseStatus(HttpStatus.CREATED)
    public BatchResponse addBatch(
            @PathVariable Long medicineId, @Valid @RequestBody CreateBatchRequest request) {
        return inventoryService.addBatch(medicineId, request);
    }

    @GetMapping("/medicines/{medicineId}/batches")
    public List<BatchResponse> getBatches(@PathVariable Long medicineId) {
        return inventoryService.getBatches(medicineId);
    }

    @GetMapping("/batches/expiring")
    public List<BatchResponse> getExpiringBatches(
            @RequestParam(defaultValue = "30") @Min(15) @Max(30) int days) {
        return inventoryService.getExpiringBatches(days);
    }
}
