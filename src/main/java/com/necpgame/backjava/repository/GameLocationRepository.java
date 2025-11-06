package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.GameLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * GameLocationRepository - репозиторий для работы с игровыми локациями.
 * 
 * Источник: API-SWAGGER/api/v1/locations/locations.yaml
 */
@Repository
public interface GameLocationRepository extends JpaRepository<GameLocationEntity, String> {

    /**
     * Найти доступные локации.
     */
    @Query("SELECT l FROM GameLocationEntity l WHERE l.accessible = true ORDER BY l.name")
    List<GameLocationEntity> findAccessibleLocations();

    /**
     * Найти локации по типу.
     */
    @Query("SELECT l FROM GameLocationEntity l WHERE l.locationType = :type ORDER BY l.dangerLevel, l.name")
    List<GameLocationEntity> findByType(String type);

    /**
     * Найти локации по уровню опасности.
     */
    @Query("SELECT l FROM GameLocationEntity l WHERE l.dangerLevel <= :maxDanger ORDER BY l.dangerLevel, l.name")
    List<GameLocationEntity> findByDangerLevelLessThanEqual(Integer maxDanger);
}

