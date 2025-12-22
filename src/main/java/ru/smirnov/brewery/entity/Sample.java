package ru.smirnov.brewery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "samples")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sample {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sample_id")
    private Integer sampleId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stage_id", nullable = false)
    private ProductionStage stage;
    
    @Column(name = "sampling_date", nullable = false)
    private LocalDateTime samplingDate;
    
    @Column(name = "status", length = 50)
    private String status = "REGISTERED";
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collected_by")
    private User collectedBy;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "sample", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Analysis> analyses;
    
    @PrePersist
    protected void onCreate() {
        if (samplingDate == null) {
            samplingDate = LocalDateTime.now();
        }
        createdAt = LocalDateTime.now();
    }
    
    public void collectSample() {
        // Метод для сбора пробы
    }
    
    public void performAnalysis() {
        // Метод для проведения анализа
    }
}

