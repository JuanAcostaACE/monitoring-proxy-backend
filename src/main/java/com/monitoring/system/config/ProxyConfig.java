package com.monitoring.system.config;

import com.monitoring.system.repository.AuditLogRepository;
import com.monitoring.system.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProxyConfig {

    @Bean(name = "inventoryProxy")
    public MicroserviceTarget inventoryProxy(InventoryMicroservice target, AuditLogRepository repo) {
        return new LoggingAuditProxy(target, repo);
    }

    @Bean(name = "orderProxy")
    public MicroserviceTarget orderProxy(OrderMicroservice target, AuditLogRepository repo) {
        return new LoggingAuditProxy(target, repo);
    }

    @Bean(name = "paymentProxy")
    public MicroserviceTarget paymentProxy(PaymentMicroservice target, AuditLogRepository repo) {
        return new LoggingAuditProxy(target, repo);
    }
}
