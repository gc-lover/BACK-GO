package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.CharacterQuestObjectiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * CharacterQuestObjectiveRepository - репозиторий для работы с прогрессом целей квестов персонажей.
 * 
 * Источник: API-SWAGGER/api/v1/quests/quests.yaml
 */
@Repository
public interface CharacterQuestObjectiveRepository extends JpaRepository<CharacterQuestObjectiveEntity, UUID> {

    /**
     * Найти прогресс всех целей квеста для персонажа.
     */
    @Query("SELECT cqo FROM CharacterQuestObjectiveEntity cqo WHERE cqo.characterId = :characterId AND cqo.questId = :questId")
    List<CharacterQuestObjectiveEntity> findByCharacterIdAndQuestId(UUID characterId, String questId);

    /**
     * Найти прогресс конкретной цели квеста для персонажа.
     */
    @Query("SELECT cqo FROM CharacterQuestObjectiveEntity cqo WHERE cqo.characterId = :characterId AND cqo.objectiveId = :objectiveId")
    Optional<CharacterQuestObjectiveEntity> findByCharacterIdAndObjectiveId(UUID characterId, String objectiveId);

    /**
     * Проверить выполнена ли цель квеста.
     */
    @Query("SELECT COUNT(cqo) > 0 FROM CharacterQuestObjectiveEntity cqo WHERE cqo.characterId = :characterId AND cqo.objectiveId = :objectiveId AND cqo.completed = true")
    boolean isObjectiveCompleted(UUID characterId, String objectiveId);

    /**
     * Посчитать выполненные цели квеста.
     */
    @Query("SELECT COUNT(cqo) FROM CharacterQuestObjectiveEntity cqo WHERE cqo.characterId = :characterId AND cqo.questId = :questId AND cqo.completed = true")
    long countCompletedObjectivesByQuest(UUID characterId, String questId);
}

