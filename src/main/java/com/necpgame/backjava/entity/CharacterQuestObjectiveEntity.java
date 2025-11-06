package com.necpgame.backjava.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * CharacterQuestObjectiveEntity - прогресс выполнения целей квеста для персонажа.
 * 
 * Отслеживает текущий прогресс каждой цели квеста для каждого персонажа.
 * Источник: API-SWAGGER/api/v1/quests/quests.yaml (QuestObjective schema)
 */
@Entity
@Table(name = "character_quest_objectives", indexes = {
    @Index(name = "idx_cq_objectives_character_quest", columnList = "character_id, quest_id"),
    @Index(name = "idx_cq_objectives_objective", columnList = "objective_id"),
    @Index(name = "idx_cq_objectives_completed", columnList = "completed")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterQuestObjectiveEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "character_id", nullable = false)
    private UUID characterId;

    @Column(name = "quest_id", nullable = false, length = 100)
    private String questId;

    @Column(name = "objective_id", nullable = false, length = 100)
    private String objectiveId;

    @Column(name = "current_progress", nullable = false)
    private Integer currentProgress = 0;

    @Column(name = "completed", nullable = false)
    private Boolean completed = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", referencedColumnName = "id", insertable = false, updatable = false)
    private CharacterEntity character;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "objective_id", referencedColumnName = "id", insertable = false, updatable = false)
    private QuestObjectiveEntity objective;
}

