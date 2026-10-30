package com.monitoring.system.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
public class OrderMicroservice implements MicroserviceTarget {

    private final Random random = new Random();

    @Override
    public String getServiceName() {
        return "ORDER_SERVICE";
    }

    @Override
    public Map<String, Object> executeRequest(String operation, Map<String, Object> payload) {
        simulateLatency(100, 400);
        if (random.nextInt(100) < 10) {
            throw new IllegalStateException("Invalid order state transition or corrupt payload");
        }
        return Map.of(
            "service", getServiceName(),
            "operation", operation,
            "orderId", UUID.randomUUID().toString().substring(0, 8),
            "orderStatus", "CREATED"
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
