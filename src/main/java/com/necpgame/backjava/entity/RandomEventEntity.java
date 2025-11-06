package com.necpgame.backjava.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * RandomEventEntity - справочник случайных событий.
 * 
 * Хранит шаблоны случайных событий в игре.
 * Источник: API-SWAGGER/api/v1/events/random-events.yaml (RandomEvent schema)
 */
@Entity
@Table(name = "random_events", indexes = {
    @Index(name = "idx_random_events_type", columnList = "event_type"),
    @Index(name = "idx_random_events_trigger", columnList = "trigger_type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RandomEventEntity {

    @Id
    @Column(name = "id", length = 100, nullable = false)
    private String id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", nullable = false, length = 2000)
    private String description;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType; // encounter, discovery, combat, social, quest

    @Column(name = "trigger_type", length = 50)
    private String triggerType; // location_enter, time_based, random, quest_related

    @Column(name = "min_level", nullable = false)
    private Integer minLevel = 1;

    @Column(name = "max_level")
    private Integer maxLevel;

    @Column(name = "rarity", nullable = false, length = 20)
    private String rarity; // common, uncommon, rare, epic, legendary

    @Column(name = "options", columnDefinition = "TEXT")
    private String options; // JSON array of EventOption

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

