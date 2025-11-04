package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.CharacterClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository для работы с классами персонажей (справочник)
 */
@Repository
public interface CharacterClassRepository extends JpaRepository<CharacterClassEntity, String> {
    
    /**
     * Найти класс по коду
     */
    Optional<CharacterClassEntity> findByClassCode(String classCode);
    
    /**
     * Получить все классы с подклассами
     */
    @Query("SELECT DISTINCT c FROM CharacterClassEntity c LEFT JOIN FETCH c.subclasses")
    List<CharacterClassEntity> findAllWithSubclasses();
}

