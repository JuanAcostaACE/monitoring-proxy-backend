package com.monitoring.system.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Random;

@Service
public class InventoryMicroservice implements MicroserviceTarget {

    private final Random random = new Random();

    @Override
    public String getServiceName() {
        return "INVENTORY_SERVICE";
    }

    @Override
    public Map<String, Object> executeRequest(String operation, Map<String, Object> payload) {
        simulateLatency(50, 250);
        if (random.nextInt(100) < 15) {
            throw new RuntimeException("Database lock timeout while checking stock for item SKU-902");
        }
        return Map.of(
            "service", getServiceName(),
            "operation", operation,
            "stockAvailable", true,
            "warehouse", "BOG-CENTRAL-01"
        );
    }

    private void simulateLatency(int min, int max) {
        try {
            Thread.sleep(random.nextInt(max - min + 1) + min);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
