package com.arthur.htit.the_high_throughput_inventory_tracker.controller;

import com.arthur.htit.the_high_throughput_inventory_tracker.dtos.PurchaseRequest;
import com.arthur.htit.the_high_throughput_inventory_tracker.service.InventoryService;
import com.arthur.htit.the_high_throughput_inventory_tracker.dtos.PurchaseResult;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/init")
    public ResponseEntity<String> initialize(@RequestParam String sku,
                                             @RequestParam long stock) {
        inventoryService.initializeStock(sku, stock);
        return ResponseEntity.ok("Initialized stock for " + sku + " = " + stock);
    }

    @GetMapping("/{sku}")
    public ResponseEntity<Long> getStock(@PathVariable String sku) {
        return ResponseEntity.ok(inventoryService.getStock(sku));
    }

    @PostMapping("/purchase")
    public ResponseEntity<PurchaseResult> purchase(@Valid @RequestBody PurchaseRequest request) {
        PurchaseResult result = inventoryService.purchase(request.getSku(), request.getQuantity());

        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        }

        if ("Insufficient stock".equals(result.getMessage())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }

        return ResponseEntity.badRequest().body(result);
    }
}