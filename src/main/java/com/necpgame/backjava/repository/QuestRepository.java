package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.QuestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * QuestRepository - репозиторий для работы с квестами.
 */
@Repository
public interface QuestRepository extends JpaRepository<QuestEntity, String> {

    /**
     * Найти квест по ID.
     *
     * @param id ID квеста
     * @return квест
     */
    Optional<QuestEntity> findById(String id);

    /**
     * Найти все квесты определенного типа.
     *
     * @param type тип квеста
     * @return список квестов
     */
    List<QuestEntity> findByType(QuestEntity.QuestType type);

    /**
     * Найти все квесты от NPC.
     *
     * @param giverNpcId ID NPC
     * @return список квестов
     */
    List<QuestEntity> findByGiverNpcId(String giverNpcId);

    /**
     * Найти все квесты для уровня.
     *
     * @param level уровень
     * @return список квестов
     */
    List<QuestEntity> findByLevel(Integer level);

    /**
     * Найти квесты подходящие для уровня.
     *
     * @param level уровень персонажа
     * @return список квестов
     */
    List<QuestEntity> findByLevelLessThanEqual(Integer level);

    /**
     * Найти первый квест для новых игроков.
     *
     * @return первый квест
     */
    default Optional<QuestEntity> findFirstQuest() {
        return findById("quest-delivery-001");
    }
}

