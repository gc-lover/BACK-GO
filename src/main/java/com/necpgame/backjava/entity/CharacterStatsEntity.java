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
 * CharacterStatsEntity - базовые характеристики персонажа (сила, рефлексы, интеллект, техника, хладнокровие).
 * 
 * Источник: API-SWAGGER/api/v1/characters/status.yaml (CharacterStats schema)
 */
@Entity
@Table(name = "character_stats", indexes = {
    @Index(name = "idx_character_stats_character", columnList = "character_id", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterStatsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "character_id", nullable = false, unique = true)
    private UUID characterId;

    @Column(name = "strength", nullable = false)
    private Integer strength = 3;

    @Column(name = "reflexes", nullable = false)
    private Integer reflexes = 3;

    @Column(name = "intelligence", nullable = false)
    private Integer intelligence = 3;

    @Column(name = "technical", nullable = false)
    private Integer technical = 3;

    @Column(name = "cool", nullable = false)
    private Integer cool = 3;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Relationship
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", referencedColumnName = "id", insertable = false, updatable = false)
    private CharacterEntity character;
}

