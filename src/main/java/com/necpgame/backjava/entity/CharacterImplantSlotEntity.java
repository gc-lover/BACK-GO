package com.necpgame.backjava.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * JPA Entity для слотов имплантов персонажа по типам.
 * 
 * Связанная таблица: character_implant_slots
 * Источник: API-SWAGGER/api/v1/gameplay/combat/implants-limits.yaml
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "character_implant_slots",
    indexes = {
        @Index(name = "idx_character_implant_slots_character", columnList = "character_id"),
        @Index(name = "idx_character_implant_slots_type", columnList = "slot_type")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_character_slot_type", columnNames = {"character_id", "slot_type"})
    }
)
public class CharacterImplantSlotEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "character_id", nullable = false, 
                foreignKey = @ForeignKey(name = "fk_character_implant_slots_character"))
    private CharacterEntity character;
    
    /**
     * Тип слота: neural, skeletal, optical, circulatory, dermal, internal
     */
    @Column(name = "slot_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private SlotType slotType;
    
    @Column(name = "max_slots", nullable = false)
    private Integer maxSlots = 2;
    
    @Column(name = "used_slots", nullable = false)
    private Integer usedSlots = 0;
    
    @Column(name = "bonus_slots", nullable = false)
    private Integer bonusSlots = 0;
    
    public enum SlotType {
        neural, skeletal, optical, circulatory, dermal, internal
    }
}

