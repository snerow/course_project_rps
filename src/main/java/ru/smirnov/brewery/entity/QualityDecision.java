package ru.smirnov.brewery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "quality_decisions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QualityDecision {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "decision_id")
    private Integer decisionId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;
    
    @Column(name = "decision", nullable = false, length = 50)
    private String decision; // APPROVED или REJECTED
    
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;
    
    @Column(name = "approved")
    private Boolean approved = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decided_by")
    private User decidedBy;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public void approvePartial() {
        decision = "APPROVED";
        approved = true;
    }
    
    public void rejectPartial() {
        decision = "REJECTED";
        approved = false;
    }
}

