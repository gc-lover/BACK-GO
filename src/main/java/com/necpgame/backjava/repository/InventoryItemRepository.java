package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.InventoryItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * InventoryItemRepository - репозиторий для работы с предметами.
 * 
 * Источник: API-SWAGGER/api/v1/inventory/inventory.yaml
 */
@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItemEntity, String> {

    /**
     * Найти предметы по категории.
     */
    @Query("SELECT i FROM InventoryItemEntity i WHERE i.category = :category")
    List<InventoryItemEntity> findByCategory(InventoryItemEntity.ItemCategory category);

    /**
     * Найти экипируемые предметы по типу слота.
     */
    @Query("SELECT i FROM InventoryItemEntity i WHERE i.equippable = true AND i.slotType = :slotType")
    List<InventoryItemEntity> findEquippableBySlotType(String slotType);

    /**
     * Найти используемые предметы.
     */
    @Query("SELECT i FROM InventoryItemEntity i WHERE i.usable = true")
    List<InventoryItemEntity> findUsableItems();

    /**
     * Найти квестовые предметы.
     */
    @Query("SELECT i FROM InventoryItemEntity i WHERE i.questItem = true")
    List<InventoryItemEntity> findQuestItems();

    /**
     * Найти предметы по редкости.
     */
    @Query("SELECT i FROM InventoryItemEntity i WHERE i.rarity = :rarity")
    List<InventoryItemEntity> findByRarity(InventoryItemEntity.ItemRarity rarity);
}

