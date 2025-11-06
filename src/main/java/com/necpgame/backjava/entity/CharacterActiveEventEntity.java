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
 * CharacterActiveEventEntity - активное событие персонажа.
 * 
 * Хранит информацию о текущих активных событиях персонажа.
 * Источник: API-SWAGGER/api/v1/events/random-events.yaml
 */
@Entity
@Table(name = "character_active_events", indexes = {
    @Index(name = "idx_character_active_events_character", columnList = "character_id"),
    @Index(name = "idx_character_active_events_event", columnList = "event_id"),
    @Index(name = "idx_character_active_events_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterActiveEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "character_id", nullable = false)
    private UUID characterId;

    @Column(name = "event_id", nullable = false, length = 100)
    private String eventId;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EventStatus status = EventStatus.ACTIVE;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

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
    @JoinColumn(name = "event_id", referencedColumnName = "id", insertable = false, updatable = false)
    private RandomEventEntity event;

    /**
     * Статус события
     */
    public enum EventStatus {
        ACTIVE,
        COMPLETED,
        EXPIRED,
        IGNORED
    }
}

