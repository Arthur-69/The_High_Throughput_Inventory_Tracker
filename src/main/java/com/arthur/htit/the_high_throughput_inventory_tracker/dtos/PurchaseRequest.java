package com.arthur.htit.the_high_throughput_inventory_tracker.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
public class PurchaseRequest {
        @NotBlank
        private String sku;

        @Min(1)
        private int quantity;

}