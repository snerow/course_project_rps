package ru.smirnov.brewery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "deviation_alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviationAlert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private Integer alertId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sample_id", nullable = false)
    private Sample sample;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "analysis_id", nullable = false)
    private Analysis analysis;
    
    @Column(name = "parameter_name", nullable = false, length = 100)
    private String parameterName;
    
    @Column(name = "actual_value", nullable = false)
    private Double actualValue;
    
    @Column(name = "norm_min")
    private Double normMin;
    
    @Column(name = "norm_max")
    private Double normMax;
    
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "notified")
    private Boolean notified = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

