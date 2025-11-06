package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.RandomEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RandomEventRepository - репозиторий для работы со случайными событиями.
 * 
 * Источник: API-SWAGGER/api/v1/events/random-events.yaml
 */
@Repository
public interface RandomEventRepository extends JpaRepository<RandomEventEntity, String> {

    /**
     * Найти события по типу.
     */
    @Query("SELECT e FROM RandomEventEntity e WHERE e.eventType = :type AND e.active = true ORDER BY e.rarity, e.title")
    List<RandomEventEntity> findByType(String type);

    /**
     * Найти активные события для уровня.
     */
    @Query("SELECT e FROM RandomEventEntity e WHERE e.active = true AND e.minLevel <= :level AND (e.maxLevel IS NULL OR e.maxLevel >= :level) ORDER BY e.rarity")
    List<RandomEventEntity> findByLevel(Integer level);

    /**
     * Найти все активные события.
     */
    @Query("SELECT e FROM RandomEventEntity e WHERE e.active = true ORDER BY e.rarity, e.title")
    List<RandomEventEntity> findAllActive();
}

