package com.arthur.htit.the_high_throughput_inventory_tracker;

import com.arthur.htit.the_high_throughput_inventory_tracker.dtos.PurchaseResult;
import com.arthur.htit.the_high_throughput_inventory_tracker.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TheHighThroughputInventoryTrackerApplicationTests {

    @Autowired
    private InventoryService inventoryService;

    private static final String SKU = "flash-001";
    private static final int INITIAL_STOCK = 500;
    private static final int TOTAL_REQUESTS = 1000;

    @BeforeEach
    void setUp() {
        inventoryService.initializeStock(SKU, INITIAL_STOCK);
    }

    @Test
    void shouldNotOversellUnderConcurrency() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(200);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(TOTAL_REQUESTS);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();

                    PurchaseResult result = inventoryService.purchase(SKU, 1);
                    if (result.isSuccess()) {
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();

        assertTrue(doneLatch.await(30, TimeUnit.SECONDS));

        executorService.shutdown();
        assertTrue(executorService.awaitTermination(10, TimeUnit.SECONDS));

        long remainingStock = inventoryService.getStock(SKU);

        assertEquals(INITIAL_STOCK, successCount.get());
        assertEquals(TOTAL_REQUESTS - INITIAL_STOCK, failCount.get());
        assertEquals(0L, remainingStock);
    }
}
