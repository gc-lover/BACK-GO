package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * LocationRepository - репозиторий для работы с локациями.
 */
@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, String> {

    /**
     * Найти локацию по ID.
     *
     * @param id ID локации
     * @return локация
     */
    Optional<LocationEntity> findById(String id);

    /**
     * Найти все локации по городу.
     *
     * @param city город
     * @return список локаций
     */
    List<LocationEntity> findByCity(String city);

    /**
     * Найти все локации по району.
     *
     * @param district район
     * @return список локаций
     */
    List<LocationEntity> findByDistrict(String district);

    /**
     * Найти локации по уровню опасности.
     *
     * @param dangerLevel уровень опасности
     * @return список локаций
     */
    List<LocationEntity> findByDangerLevel(LocationEntity.DangerLevel dangerLevel);

    /**
     * Найти стартовую локацию (Downtown).
     *
     * @return стартовая локация
     */
    default Optional<LocationEntity> findStartingLocation() {
        return findById("loc-downtown-001");
    }
}

