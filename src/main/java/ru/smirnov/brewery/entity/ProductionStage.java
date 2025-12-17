package ru.smirnov.brewery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "production_stages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionStage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stage_id")
    private Integer stageId;
    
    @Column(name = "stage_name", nullable = false, unique = true, length = 100)
    private String stageName;
    
    @Column(name = "norms_ph_min")
    private Double normsPHMin;
    
    @Column(name = "norms_ph_max")
    private Double normsPHMax;
    
    @Column(name = "norms_density_min")
    private Double normsDensityMin;
    
    @Column(name = "norms_density_max")
    private Double normsDensityMax;
    
    @Column(name = "norms_alcohol_min")
    private Double normsAlcoholMin;
    
    @Column(name = "norms_alcohol_max")
    private Double normsAlcoholMax;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "stage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Sample> samples;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public String getStageName() {
        return stageName;
    }
}

