package com.monitoring.system.service;

import com.monitoring.system.model.AuditLog;
import com.monitoring.system.repository.AuditLogRepository;
import java.time.LocalDateTime;
import java.util.Map;

public class LoggingAuditProxy implements MicroserviceTarget {

    private final MicroserviceTarget targetService;
    private final AuditLogRepository auditLogRepository;

    public LoggingAuditProxy(MicroserviceTarget targetService, AuditLogRepository auditLogRepository) {
        this.targetService = targetService;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public String getServiceName() {
        return targetService.getServiceName();
    }

    @Override
    public Map<String, Object> executeRequest(String operation, Map<String, Object> payload) {
        long startTime = System.currentTimeMillis();
        String status = "SUCCESS";
        String message = "Operation executed successfully";

        try {
            return targetService.executeRequest(operation, payload);
        } catch (Exception ex) {
            status = "ERROR";
            message = ex.getMessage();
            throw ex;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            AuditLog auditEntry = AuditLog.builder()
                    .serviceName(targetService.getServiceName())
                    .operation(operation)
                    .responseTimeMs(duration)
                    .status(status)
                    .message(message)
                    .timestamp(LocalDateTime.now())
                    .build();
            auditLogRepository.save(auditEntry);
        }
    }
}
