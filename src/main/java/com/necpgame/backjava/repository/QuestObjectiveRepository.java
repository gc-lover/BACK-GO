package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.QuestObjectiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * QuestObjectiveRepository - репозиторий для работы с целями квестов.
 * 
 * Источник: API-SWAGGER/api/v1/quests/quests.yaml
 */
@Repository
public interface QuestObjectiveRepository extends JpaRepository<QuestObjectiveEntity, String> {

    /**
     * Найти все цели квеста.
     */
    @Query("SELECT o FROM QuestObjectiveEntity o WHERE o.questId = :questId ORDER BY o.orderIndex")
    List<QuestObjectiveEntity> findByQuestIdOrderByOrderIndex(String questId);

    /**
     * Найти обязательные цели квеста.
     */
    @Query("SELECT o FROM QuestObjectiveEntity o WHERE o.questId = :questId AND o.optional = false ORDER BY o.orderIndex")
    List<QuestObjectiveEntity> findRequiredByQuestId(String questId);

    /**
     * Найти опциональные цели квеста.
     */
    @Query("SELECT o FROM QuestObjectiveEntity o WHERE o.questId = :questId AND o.optional = true ORDER BY o.orderIndex")
    List<QuestObjectiveEntity> findOptionalByQuestId(String questId);
}

