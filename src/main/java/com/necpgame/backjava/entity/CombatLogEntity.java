package com.necpgame.backjava.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * CombatLogEntity - лог боя (история действий).
 * 
 * Хранит историю всех действий в боевой сессии.
 * Источник: API-SWAGGER/api/v1/combat/combat.yaml (CombatState.log)
 */
@Entity
@Table(name = "combat_log", indexes = {
    @Index(name = "idx_combat_log_session", columnList = "combat_session_id"),
    @Index(name = "idx_combat_log_round", columnList = "round")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CombatLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "combat_session_id", nullable = false)
    private UUID combatSessionId;

    @Column(name = "round", nullable = false)
    private Integer round;

    @Column(name = "action_order", nullable = false)
    private Integer actionOrder; // Порядок действия в раунде

    @Column(name = "actor_id", length = 100)
    private String actorId;

    @Column(name = "action_type", length = 50)
    private String actionType; // attack, defend, use_item, ability, flee

    @Column(name = "target_id", length = 100)
    private String targetId;

    @Column(name = "message", nullable = false, length = 500)
    private String message; // "Player атаковал Enemy и нанес 25 урона"

    @Column(name = "damage_dealt")
    private Integer damageDealt;

    @Column(name = "healing_done")
    private Integer healingDone;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "combat_session_id", referencedColumnName = "id", insertable = false, updatable = false)
    private CombatSessionEntity combatSession;
}

