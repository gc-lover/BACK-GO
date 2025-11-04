package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.CharacterAppearanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository для работы с внешностью персонажей
 */
@Repository
public interface CharacterAppearanceRepository extends JpaRepository<CharacterAppearanceEntity, UUID> {
    // Базовые CRUD операции от JpaRepository
}

