package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.TutorialProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * TutorialProgressRepository - репозиторий для работы с прогрессом туториала.
 */
@Repository
public interface TutorialProgressRepository extends JpaRepository<TutorialProgressEntity, UUID> {

    /**
     * Найти прогресс туториала для персонажа.
     *
     * @param characterId ID персонажа
     * @return прогресс туториала
     */
    Optional<TutorialProgressEntity> findByCharacterId(UUID characterId);

    /**
     * Проверить существование прогресса туториала для персонажа.
     *
     * @param characterId ID персонажа
     * @return true, если прогресс существует
     */
    boolean existsByCharacterId(UUID characterId);

    /**
     * Удалить прогресс туториала для персонажа.
     *
     * @param characterId ID персонажа
     */
    void deleteByCharacterId(UUID characterId);
}

