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
 * CharacterInventoryEntity - предметы в инвентаре персонажа.
 * 
 * Хранит информацию о предметах в инвентаре каждого персонажа (количество, позиция).
 * Источник: API-SWAGGER/api/v1/inventory/inventory.yaml
 */
@Entity
@Table(name = "character_inventory", indexes = {
    @Index(name = "idx_character_inventory_character", columnList = "character_id"),
    @Index(name = "idx_character_inventory_item", columnList = "item_id"),
    @Index(name = "idx_character_inventory_character_item", columnList = "character_id, item_id", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterInventoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "character_id", nullable = false)
    private UUID characterId;

    @Column(name = "item_id", nullable = false, length = 100)
    private String itemId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "slot_position")
    private Integer slotPosition;

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
}

