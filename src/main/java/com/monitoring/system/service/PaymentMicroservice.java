package com.monitoring.system.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Random;

@Service
public class PaymentMicroservice implements MicroserviceTarget {

    private final Random random = new Random();

    @Override
    public String getServiceName() {
        return "PAYMENT_SERVICE";
    }

    @Override
    public Map<String, Object> executeRequest(String operation, Map<String, Object> payload) {
        simulateLatency(200, 750);
        if (random.nextInt(100) < 25) {
            throw new RuntimeException("Payment Gateway 502 Bad Gateway: Transaction declined by issuer");
        }
        return Map.of(
            "service", getServiceName(),
            "operation", operation,
            "transactionStatus", "APPROVED",
            "authorizationCode", "AUTH-" + (100000 + random.nextInt(900000))
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
