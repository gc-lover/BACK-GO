package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.VendorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * VendorRepository - репозиторий для работы с торговцами.
 * 
 * Источник: API-SWAGGER/api/v1/trading/trading.yaml
 */
@Repository
public interface VendorRepository extends JpaRepository<VendorEntity, String> {

    /**
     * Найти торговцев в локации.
     */
    @Query("SELECT v FROM VendorEntity v WHERE v.locationId = :locationId AND v.available = true ORDER BY v.name")
    List<VendorEntity> findByLocationId(String locationId);

    /**
     * Найти торговцев по типу.
     */
    @Query("SELECT v FROM VendorEntity v WHERE v.vendorType = :type AND v.available = true ORDER BY v.name")
    List<VendorEntity> findByType(String type);

    /**
     * Найти доступных торговцев.
     */
    @Query("SELECT v FROM VendorEntity v WHERE v.available = true ORDER BY v.name")
    List<VendorEntity> findAllAvailable();
}

