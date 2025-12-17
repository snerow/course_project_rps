package ru.smirnov.brewery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;
    
    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;
    
    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    
    @Column(name = "enabled")
    private Boolean enabled = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "collectedBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Sample> samples;
    
    @OneToMany(mappedBy = "performedBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Analysis> analyses;
    
    @OneToMany(mappedBy = "decidedBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QualityDecision> qualityDecisions;
    
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Batch> batches;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public boolean login() {
        return enabled != null && enabled;
    }
    
    public void manageUsers() {
        // Метод для управления пользователями
    }
}

