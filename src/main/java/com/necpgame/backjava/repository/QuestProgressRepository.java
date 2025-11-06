package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.QuestProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * QuestProgressRepository - репозиторий для работы с прогрессом квестов.
 */
@Repository
public interface QuestProgressRepository extends JpaRepository<QuestProgressEntity, UUID> {

    /**
     * Найти прогресс квеста для персонажа.
     *
     * @param characterId ID персонажа
     * @param questId ID квеста
     * @return прогресс квеста
     */
    Optional<QuestProgressEntity> findByCharacterIdAndQuestId(UUID characterId, String questId);

    /**
     * Найти все квесты персонажа.
     *
     * @param characterId ID персонажа
     * @return список прогресса квестов
     */
    List<QuestProgressEntity> findByCharacterId(UUID characterId);

    /**
     * Найти активные квесты персонажа.
     *
     * @param characterId ID персонажа
     * @return список активных квестов
     */
    List<QuestProgressEntity> findByCharacterIdAndStatus(UUID characterId, QuestProgressEntity.QuestStatus status);

    /**
     * Проверить существование прогресса квеста.
     *
     * @param characterId ID персонажа
     * @param questId ID квеста
     * @return true, если прогресс существует
     */
    boolean existsByCharacterIdAndQuestId(UUID characterId, String questId);

    /**
     * Найти завершенные квесты персонажа.
     *
     * @param characterId ID персонажа
     * @return список завершенных квестов
     */
    default List<QuestProgressEntity> findCompletedQuestsByCharacterId(UUID characterId) {
        return findByCharacterIdAndStatus(characterId, QuestProgressEntity.QuestStatus.COMPLETED);
    }

    /**
     * Найти активные квесты персонажа.
     *
     * @param characterId ID персонажа
     * @return список активных квестов
     */
    default List<QuestProgressEntity> findActiveQuestsByCharacterId(UUID characterId) {
        return findByCharacterIdAndStatus(characterId, QuestProgressEntity.QuestStatus.ACTIVE);
    }
}

