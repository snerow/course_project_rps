package ru.smirnov.brewery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "analyses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Analysis {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_id")
    private Integer analysisId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sample_id", nullable = false)
    private Sample sample;
    
    @Column(name = "analysis_type", nullable = false, length = 50)
    private String analysisType; // CHEMICAL или MICROBIOLOGICAL
    
    @Column(name = "ph")
    private Double pH;
    
    @Column(name = "density")
    private Double density;
    
    @Column(name = "alcohol_content")
    private Double alcoholContent;
    
    @Column(name = "within_norms")
    private Boolean withinNorms = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private User performedBy;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public void validateParameters() {
        // Метод для валидации параметров
    }
    
    public void checkStandards() {
        // Метод для проверки соответствия стандартам
        ProductionStage stage = sample.getStage();
        withinNorms = true;
        
        if (pH != null && stage.getNormsPHMin() != null && stage.getNormsPHMax() != null) {
            if (pH < stage.getNormsPHMin() || pH > stage.getNormsPHMax()) {
                withinNorms = false;
            }
        }
        
        if (density != null && stage.getNormsDensityMin() != null && stage.getNormsDensityMax() != null) {
            if (density < stage.getNormsDensityMin() || density > stage.getNormsDensityMax()) {
                withinNorms = false;
            }
        }
        
        if (alcoholContent != null && stage.getNormsAlcoholMin() != null && stage.getNormsAlcoholMax() != null) {
            if (alcoholContent < stage.getNormsAlcoholMin() || alcoholContent > stage.getNormsAlcoholMax()) {
                withinNorms = false;
            }
        }
    }
}

