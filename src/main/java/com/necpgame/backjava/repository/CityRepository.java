package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository для работы с городами (справочник)
 */
@Repository
public interface CityRepository extends JpaRepository<CityEntity, UUID> {
    
    /**
     * Найти город по ID
     */
    Optional<CityEntity> findById(UUID id);
    
    /**
     * Найти города по региону
     */
    List<CityEntity> findByRegion(String region);
    
    /**
     * Найти города, доступные для фракции
     */
    @Query("SELECT c FROM CityEntity c " +
           "JOIN c.availableFactions f " +
           "WHERE f.id = :factionId")
    List<CityEntity> findByAvailableForFaction(@Param("factionId") UUID factionId);
    
    /**
     * Найти города по региону и фракции
     */
    @Query("SELECT c FROM CityEntity c " +
           "JOIN c.availableFactions f " +
           "WHERE c.region = :region AND f.id = :factionId")
    List<CityEntity> findByRegionAndFaction(@Param("region") String region, @Param("factionId") UUID factionId);
    
    /**
     * Получить все города
     */
    List<CityEntity> findAll();
}

