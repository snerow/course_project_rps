package ru.smirnov.brewery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "quality_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QualityLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Integer logId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private Batch batch;
    
    @Column(name = "operation_type", nullable = false, length = 100)
    private String operationType;
    
    @Column(name = "operation_date", nullable = false)
    private LocalDateTime operationDate;
    
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @PrePersist
    protected void onCreate() {
        if (operationDate == null) {
            operationDate = LocalDateTime.now();
        }
    }
}

