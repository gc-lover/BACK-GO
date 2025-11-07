package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.CharacterStatsSnapshotEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CharacterStatsSnapshotRepository extends JpaRepository<CharacterStatsSnapshotEntity, UUID> {

    Optional<CharacterStatsSnapshotEntity> findByCharacterId(UUID characterId);

    void deleteByCharacterId(UUID characterId);
}
package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.CharacterStatsSnapshotEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий снепшотов данных персонажей для восстановления.
 */
@Repository
public interface CharacterStatsSnapshotRepository extends JpaRepository<CharacterStatsSnapshotEntity, UUID> {

    Optional<CharacterStatsSnapshotEntity> findByCharacterId(UUID characterId);

    void deleteByCharacterId(UUID characterId);
}

