package com.krushnag.pharmacy.api;

import com.krushnag.pharmacy.api.ApiModels.CreateMedicineRequest;
import com.krushnag.pharmacy.api.ApiModels.MedicineResponse;
import com.krushnag.pharmacy.service.PharmacyInventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    private final PharmacyInventoryService inventoryService;

    public MedicineController(PharmacyInventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedicineResponse createMedicine(@Valid @RequestBody CreateMedicineRequest request) {
        return inventoryService.createMedicine(request);
    }

    @GetMapping
    public List<MedicineResponse> getMedicines() {
        return inventoryService.getMedicines();
    }
}
