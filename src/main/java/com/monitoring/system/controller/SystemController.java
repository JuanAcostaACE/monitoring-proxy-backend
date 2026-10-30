package com.monitoring.system.controller;

import com.monitoring.system.model.AuditLog;
import com.monitoring.system.repository.AuditLogRepository;
import com.monitoring.system.service.MicroserviceTarget;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SystemController {

    private final MicroserviceTarget inventoryProxy;
    private final MicroserviceTarget orderProxy;
    private final MicroserviceTarget paymentProxy;
    private final AuditLogRepository auditLogRepository;

    public SystemController(
            @Qualifier("inventoryProxy") MicroserviceTarget inventoryProxy,
            @Qualifier("orderProxy") MicroserviceTarget orderProxy,
            @Qualifier("paymentProxy") MicroserviceTarget paymentProxy,
            AuditLogRepository auditLogRepository) {
        this.inventoryProxy = inventoryProxy;
        this.orderProxy = orderProxy;
        this.paymentProxy = paymentProxy;
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }

    @PostMapping("/inventory/{operation}")
    public ResponseEntity<?> callInventory(@PathVariable String operation, @RequestBody(required = false) Map<String, Object> payload) {
        return handleProxiedCall(inventoryProxy, operation, payload);
    }

    @PostMapping("/orders/{operation}")
    public ResponseEntity<?> callOrders(@PathVariable String operation, @RequestBody(required = false) Map<String, Object> payload) {
        return handleProxiedCall(orderProxy, operation, payload);
    }

    @PostMapping("/payments/{operation}")
    public ResponseEntity<?> callPayments(@PathVariable String operation, @RequestBody(required = false) Map<String, Object> payload) {
        return handleProxiedCall(paymentProxy, operation, payload);
    }

    private ResponseEntity<?> handleProxiedCall(MicroserviceTarget proxy, String operation, Map<String, Object> payload) {
        try {
            Map<String, Object> response = proxy.executeRequest(operation, payload != null ? payload : Map.of());
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "ERROR",
                    "service", proxy.getServiceName(),
                    "error", ex.getMessage()
            ));
        }
    }

    @GetMapping("/observability/metrics")
    public ResponseEntity<Map<String, Object>> getDashboardMetrics() {
        List<AuditLog> allLogs = auditLogRepository.findAll();
        List<AuditLog> recentLogs = auditLogRepository.findTop50ByOrderByTimestampDesc();

        long totalCalls = allLogs.size();
        long errorCount = allLogs.stream().filter(l -> "ERROR".equals(l.getStatus())).count();
        double avgLatency = allLogs.stream().mapToLong(AuditLog::getResponseTimeMs).average().orElse(0.0);

        Map<String, Long> callsByService = allLogs.stream()
                .collect(Collectors.groupingBy(AuditLog::getServiceName, Collectors.counting()));

        Map<String, Double> avgLatencyByService = allLogs.stream()
                .collect(Collectors.groupingBy(
                        AuditLog::getServiceName,
                        Collectors.averagingLong(AuditLog::getResponseTimeMs)
                ));

        Map<String, Long> errorsByService = allLogs.stream()
                .filter(l -> "ERROR".equals(l.getStatus()))
                .collect(Collectors.groupingBy(AuditLog::getServiceName, Collectors.counting()));

        return ResponseEntity.ok(Map.of(
                "totalCalls", totalCalls,
                "errorCount", errorCount,
                "errorRate", totalCalls > 0 ? (double) errorCount / totalCalls * 100 : 0.0,
                "avgLatencyMs", Math.round(avgLatency * 100.0) / 100.0,
                "callsByService", callsByService,
                "avgLatencyByService", avgLatencyByService,
                "errorsByService", errorsByService,
                "recentLogs", recentLogs
        ));
    }
}
