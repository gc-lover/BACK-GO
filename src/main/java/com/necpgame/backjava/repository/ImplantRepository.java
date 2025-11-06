package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.ImplantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository для управления имплантами (справочник).
 * 
 * Источник: API-SWAGGER/api/v1/gameplay/combat/implants-limits.yaml
 */
@Repository
public interface ImplantRepository extends JpaRepository<ImplantEntity, String> {
    
    /**
     * Найти импланты по типу.
     */
    List<ImplantEntity> findByType(ImplantEntity.ImplantType type);
    
    /**
     * Найти импланты по типу слота.
     */
    List<ImplantEntity> findBySlotType(ImplantEntity.SlotType slotType);
    
    /**
     * Найти импланты по редкости.
     */
    List<ImplantEntity> findByRarity(ImplantEntity.Rarity rarity);
    
    /**
     * Найти импланты доступные для уровня.
     */
    @Query("SELECT i FROM ImplantEntity i WHERE i.minLevel <= :level")
    List<ImplantEntity> findAvailableForLevel(Integer level);
    
    /**
     * Проверить существование импланта.
     */
    boolean existsById(String id);
}

