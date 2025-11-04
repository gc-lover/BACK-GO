package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.CharacterOriginEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository для работы с происхождениями персонажей (справочник)
 */
@Repository
public interface CharacterOriginRepository extends JpaRepository<CharacterOriginEntity, String> {
    
    /**
     * Найти происхождение по коду
     */
    Optional<CharacterOriginEntity> findByOriginCode(String originCode);
    
    /**
     * Получить все происхождения с доступными фракциями
     */
    @Query("SELECT DISTINCT o FROM CharacterOriginEntity o LEFT JOIN FETCH o.availableFactions")
    List<CharacterOriginEntity> findAllWithFactions();
}

