package com.krushnag.pharmacy.api;

import com.krushnag.pharmacy.api.ApiModels.SaleResponse;
import com.krushnag.pharmacy.api.ApiModels.SellRequest;
import com.krushnag.pharmacy.service.PharmacyInventoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/medicines/{medicineId}/sales")
public class SaleController {

    private final PharmacyInventoryService inventoryService;

    public SaleController(PharmacyInventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public SaleResponse sell(
            @PathVariable Long medicineId, @Valid @RequestBody SellRequest request) {
        return inventoryService.sell(medicineId, request.quantity());
    }
}
