package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.VendorInventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * VendorInventoryRepository - репозиторий для работы с инвентарем торговца.
 * 
 * Источник: API-SWAGGER/api/v1/trading/trading.yaml
 */
@Repository
public interface VendorInventoryRepository extends JpaRepository<VendorInventoryEntity, UUID> {

    /**
     * Найти весь инвентарь торговца.
     */
    @Query("SELECT vi FROM VendorInventoryEntity vi WHERE vi.vendorId = :vendorId AND vi.available = true ORDER BY vi.item.name")
    List<VendorInventoryEntity> findByVendorId(String vendorId);

    /**
     * Найти конкретный предмет у торговца.
     */
    @Query("SELECT vi FROM VendorInventoryEntity vi WHERE vi.vendorId = :vendorId AND vi.itemId = :itemId")
    Optional<VendorInventoryEntity> findByVendorIdAndItemId(String vendorId, String itemId);

    /**
     * Проверить есть ли предмет у торговца.
     */
    @Query("SELECT COUNT(vi) > 0 FROM VendorInventoryEntity vi WHERE vi.vendorId = :vendorId AND vi.itemId = :itemId AND vi.available = true")
    boolean existsByVendorIdAndItemId(String vendorId, String itemId);
}

