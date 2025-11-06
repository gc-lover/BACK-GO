package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.SkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * SkillRepository - репозиторий для работы со справочником навыков.
 * 
 * Источник: API-SWAGGER/api/v1/characters/status.yaml
 */
@Repository
public interface SkillRepository extends JpaRepository<SkillEntity, String> {

    /**
     * Найти навыки по категории.
     */
    @Query("SELECT s FROM SkillEntity s WHERE s.category = :category ORDER BY s.name")
    List<SkillEntity> findByCategory(String category);

    /**
     * Найти все навыки, отсортированные по имени.
     */
    @Query("SELECT s FROM SkillEntity s ORDER BY s.category, s.name")
    List<SkillEntity> findAllOrderByName();
}

