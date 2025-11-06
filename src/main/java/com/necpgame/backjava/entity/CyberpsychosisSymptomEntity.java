package com.necpgame.backjava.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * JPA Entity для симптомов киберпсихоза (справочник).
 * 
 * Связанная таблица: cyberpsychosis_symptoms
 * Источник: API-SWAGGER/api/v1/gameplay/combat/cyberpsychosis.yaml
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "cyberpsychosis_symptoms",
    indexes = {
        @Index(name = "idx_cyberpsychosis_symptoms_stage", columnList = "stage"),
        @Index(name = "idx_cyberpsychosis_symptoms_severity", columnList = "severity")
    }
)
public class CyberpsychosisSymptomEntity {
    
    @Id
    @Column(name = "id", length = 100, nullable = false)
    private String id;
    
    @Column(name = "name", nullable = false, length = 200)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /**
     * Стадия: early, middle, late, cyberpsychosis
     */
    @Column(name = "stage", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private Stage stage;
    
    /**
     * Серьезность: mild, moderate, severe, critical
     */
    @Column(name = "severity", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private Severity severity;
    
    /**
     * Категория: behavioral, physical, mental, social
     */
    @Column(name = "category", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private Category category;
    
    @Column(name = "stat_effects", columnDefinition = "JSONB")
    private String statEffects;
    
    @Column(name = "chance_to_trigger", nullable = false)
    private Integer chanceToTrigger = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public enum Stage {
        early, middle, late, cyberpsychosis
    }
    
    public enum Severity {
        mild, moderate, severe, critical
    }
    
    public enum Category {
        behavioral, physical, mental, social
    }
}

