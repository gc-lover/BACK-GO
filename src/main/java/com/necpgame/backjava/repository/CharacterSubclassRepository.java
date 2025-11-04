package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.CharacterSubclassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository для работы с подклассами персонажей (справочник)
 */
@Repository
public interface CharacterSubclassRepository extends JpaRepository<CharacterSubclassEntity, String> {
    
    /**
     * Найти подкласс по коду
     */
    Optional<CharacterSubclassEntity> findBySubclassCode(String subclassCode);
    
    /**
     * Найти все подклассы для класса
     */
    List<CharacterSubclassEntity> findAllByCharacterClass_ClassCode(String classCode);
}

