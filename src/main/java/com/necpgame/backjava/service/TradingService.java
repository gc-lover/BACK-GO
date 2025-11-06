package com.necpgame.backjava.service;

import com.necpgame.backjava.model.*;

import java.util.UUID;

/**
 * TradingService - сервис для работы с торговлей.
 * 
 * Сгенерировано на основе: API-SWAGGER/api/v1/trading/trading.yaml
 */
public interface TradingService {

    /**
     * Получить список торговцев.
     */
    GetVendors200Response getVendors(UUID characterId, String locationId);

    /**
     * Получить инвентарь торговца.
     */
    VendorInventory getVendorInventory(String vendorId, UUID characterId);

    /**
     * Купить предмет у торговца.
     */
    BuyItem200Response buyItem(BuyItemRequest request);

    /**
     * Продать предмет торговцу.
     */
    SellItem200Response sellItem(BuyItemRequest request);

    /**
     * Получить цену предмета.
     */
    GetItemPrice200Response getItemPrice(String itemId, String vendorId, UUID characterId);
}

