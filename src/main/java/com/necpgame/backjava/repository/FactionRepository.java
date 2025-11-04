package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.FactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository для работы с фракциями (справочник)
 */
@Repository
public interface FactionRepository extends JpaRepository<FactionEntity, UUID> {
    
    /**
     * Найти фракцию по ID
     */
    Optional<FactionEntity> findById(UUID id);
    
    /**
     * Найти фракции, доступные для происхождения
     */
    @Query("SELECT f FROM FactionEntity f " +
           "JOIN f.origins o " +
           "WHERE o.originCode = :originCode")
    List<FactionEntity> findByAvailableForOrigin(@Param("originCode") String originCode);
    
    /**
     * Получить все фракции
     */
    List<FactionEntity> findAll();
}

