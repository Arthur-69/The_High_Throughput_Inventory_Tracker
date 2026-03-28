package com.arthur.htit.the_high_throughput_inventory_tracker.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PurchaseResult {

    private final boolean success;
    private final String message;
    private final Long remainingStock;

    public static PurchaseResult success(Long remainingStock) {
        return new PurchaseResult(true, "Purchase successful", remainingStock);
    }

    public static PurchaseResult soldOut(String message) {
        return new PurchaseResult(false, message, null);
    }

    public static PurchaseResult invalid(String message) {
        return new PurchaseResult(false, message, null);
    }

    public static PurchaseResult error(String message) {
        return new PurchaseResult(false, message, null);
    }

}