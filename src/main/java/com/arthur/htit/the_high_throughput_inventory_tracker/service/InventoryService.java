package com.arthur.htit.the_high_throughput_inventory_tracker.service;

import java.util.Collections;

import com.arthur.htit.the_high_throughput_inventory_tracker.dtos.PurchaseResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    private final RedisTemplate<String, Long> redisTemplate;
    private final DefaultRedisScript<Long> decrementInventoryScript;

    public InventoryService(RedisTemplate<String, Long> redisTemplate,
                            DefaultRedisScript<Long> decrementInventoryScript) {
        this.redisTemplate = redisTemplate;
        this.decrementInventoryScript = decrementInventoryScript;
    }

    public void initializeStock(String sku, long stock) {
        redisTemplate.opsForValue().set(stockKey(sku), stock);
    }

    public PurchaseResult purchase(String sku, int quantity) {
        if (quantity <= 0) {
            return PurchaseResult.invalid("Quantity must be greater than 0");
        }

        Long result = redisTemplate.execute(
                decrementInventoryScript,
                Collections.singletonList(stockKey(sku)),
                String.valueOf(quantity)
        );

        if (result == null) {
            return PurchaseResult.error("Redis returned null");
        }

        if (result == -1L) {
            return PurchaseResult.soldOut("Insufficient stock");
        }

        return PurchaseResult.success(result);
    }

    public long getStock(String sku) {
        Long value = redisTemplate.opsForValue().get(stockKey(sku));
        return value == null ? 0L : value;
    }

    private String stockKey(String sku) {
        return "inventory:item:" + sku;
    }
}