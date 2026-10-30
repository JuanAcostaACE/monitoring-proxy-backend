package com.monitoring.system.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String serviceName;

    @Column(nullable = false)
    private String operation;

    @Column(nullable = false)
    private Long responseTimeMs;

    @Column(nullable = false)
    private String status;

    @Column(length = 500)
    private String message;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
