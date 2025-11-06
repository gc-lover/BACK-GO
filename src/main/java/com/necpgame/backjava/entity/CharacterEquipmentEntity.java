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
 * CharacterEquipmentEntity - СЌРєРёРїРёСЂРѕРІР°РЅРЅС‹Рµ РїСЂРµРґРјРµС‚С‹ РїРµСЂСЃРѕРЅР°Р¶Р°.
 * 
 * РҐСЂР°РЅРёС‚ РёРЅС„РѕСЂРјР°С†РёСЋ Рѕ СЌРєРёРїРёСЂРѕРІР°РЅРЅС‹С… РїСЂРµРґРјРµС‚Р°С… РІ СЃР»РѕС‚Р°С… РїРµСЂСЃРѕРЅР°Р¶Р°.
 * РСЃС‚РѕС‡РЅРёРє: API-SWAGGER/api/v1/inventory/inventory.yaml (EquipmentSlot schema)
 */
@Entity
@Table(name = "character_equipment", indexes = {
    @Index(name = "idx_character_equipment_character", columnList = "character_id"),
    @Index(name = "idx_character_equipment_slot", columnList = "slot_type"),
    @Index(name = "idx_character_equipment_character_slot", columnList = "character_id, slot_type", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterEquipmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "character_id", nullable = false)
    private UUID characterId;

    @Column(name = "slot_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private SlotType slotType;

    @Column(name = "item_id", length = 100)
    private String itemId;

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
    @JoinColumn(name = "item_id", referencedColumnName = "id", insertable = false, updatable = false)
    private InventoryItemEntity item;

    /**
     * РўРёРї СЃР»РѕС‚Р° СЌРєРёРїРёСЂРѕРІРєРё (РёР· OpenAPI - EquipmentSlot.slotType enum)
     */
    public enum SlotType {
        HEAD,
        BODY,
        HANDS,
        LEGS,
        WEAPON_PRIMARY,
        WEAPON_SECONDARY,
        IMPLANT_1,
        IMPLANT_2,
        IMPLANT_3
    }
}

