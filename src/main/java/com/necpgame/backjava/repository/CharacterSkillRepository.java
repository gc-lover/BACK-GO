package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.CharacterSkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * CharacterSkillRepository - репозиторий для работы с навыками персонажа.
 * 
 * Источник: API-SWAGGER/api/v1/characters/status.yaml
 */
@Repository
public interface CharacterSkillRepository extends JpaRepository<CharacterSkillEntity, UUID> {

    /**
     * Найти все навыки персонажа.
     */
    @Query("SELECT cs FROM CharacterSkillEntity cs WHERE cs.characterId = :characterId ORDER BY cs.level DESC, cs.experience DESC")
    List<CharacterSkillEntity> findByCharacterIdOrderByLevelDesc(UUID characterId);

    /**
     * Найти конкретный навык персонажа.
     */
    @Query("SELECT cs FROM CharacterSkillEntity cs WHERE cs.characterId = :characterId AND cs.skillId = :skillId")
    Optional<CharacterSkillEntity> findByCharacterIdAndSkillId(UUID characterId, String skillId);

    /**
     * Проверить есть ли у персонажа навык.
     */
    @Query("SELECT COUNT(cs) > 0 FROM CharacterSkillEntity cs WHERE cs.characterId = :characterId AND cs.skillId = :skillId")
    boolean existsByCharacterIdAndSkillId(UUID characterId, String skillId);

    /**
     * Посчитать количество навыков персонажа.
     */
    @Query("SELECT COUNT(cs) FROM CharacterSkillEntity cs WHERE cs.characterId = :characterId")
    long countByCharacterId(UUID characterId);
}

