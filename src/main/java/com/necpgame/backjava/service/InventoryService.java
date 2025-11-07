package com.necpgame.backjava.service;

import com.necpgame.backjava.model.AutoSortRequest;
import com.necpgame.backjava.model.CleanupRequest;
import com.necpgame.backjava.model.Container;
import com.necpgame.backjava.model.Error;
import com.necpgame.backjava.model.Inventory;
import com.necpgame.backjava.model.InventoryError;
import com.necpgame.backjava.model.InventoryFiltersResponse;
import com.necpgame.backjava.model.InventoryHistoryResponse;
import com.necpgame.backjava.model.ItemConsumeRequest;
import com.necpgame.backjava.model.ItemDropRequest;
import com.necpgame.backjava.model.ItemEquipRequest;
import com.necpgame.backjava.model.ItemMergeRequest;
import com.necpgame.backjava.model.ItemMoveRequest;
import com.necpgame.backjava.model.ItemOperationResult;
import com.necpgame.backjava.model.ItemPickupRequest;
import com.necpgame.backjava.model.ItemSplitRequest;
import com.necpgame.backjava.model.ItemTemplate;
import com.necpgame.backjava.model.ItemUnequipRequest;
import org.springframework.lang.Nullable;
import com.necpgame.backjava.model.Reservation;
import com.necpgame.backjava.model.ReservationReleaseRequest;
import com.necpgame.backjava.model.ReservationRequest;
import com.necpgame.backjava.model.StashTransferRequest;
import com.necpgame.backjava.model.WeightInfo;
import org.springframework.validation.annotation.Validated;

/**
 * Service interface for InventoryService.
 * Generated from OpenAPI specification.
 * 
 * This is a service interface that should be implemented by a service implementation class.
 */
@Validated
public interface InventoryService {

    /**
     * POST /inventory/players/{playerId}/auto-sort : Автоматическая сортировка инвентаря
     *
     * @param playerId Идентификатор игрока/персонажа (required)
     * @param autoSortRequest  (required)
     * @return Void
     */
    Void inventoryPlayersPlayerIdAutoSortPost(String playerId, AutoSortRequest autoSortRequest);

    /**
     * POST /inventory/players/{playerId}/cleanup : Удалить предметы класса junk
     *
     * @param playerId Идентификатор игрока/персонажа (required)
     * @param cleanupRequest  (required)
     * @return Void
     */
    Void inventoryPlayersPlayerIdCleanupPost(String playerId, CleanupRequest cleanupRequest);

    /**
     * GET /inventory/players/{playerId}/containers/{containerId} : Получить содержимое контейнера
     *
     * @param playerId Идентификатор игрока/персонажа (required)
     * @param containerId Идентификатор контейнера инвентаря (required)
     * @return Container
     */
    Container inventoryPlayersPlayerIdContainersContainerIdGet(String playerId, String containerId);

    /**
     * GET /inventory/players/{playerId}/filters : Активные фильтры и пресеты
     *
     * @param playerId Идентификатор игрока/персонажа (required)
     * @return InventoryFiltersResponse
     */
    InventoryFiltersResponse inventoryPlayersPlayerIdFiltersGet(String playerId);

    /**
     * GET /inventory/players/{playerId} : Получить состояние инвентаря игрока
     *
     * @param playerId Идентификатор игрока/персонажа (required)
     * @return Inventory
     */
    Inventory inventoryPlayersPlayerIdGet(String playerId);

    /**
     * GET /inventory/players/{playerId}/history : Журнал операций с инвентарём
     *
     * @param playerId Идентификатор игрока/персонажа (required)
     * @param event  (optional)
     * @param page Номер страницы (начинается с 1) (optional, default to 1)
     * @param pageSize Количество элементов на странице (optional, default to 20)
     * @return InventoryHistoryResponse
     */
    InventoryHistoryResponse inventoryPlayersPlayerIdHistoryGet(String playerId, String event, Integer page, Integer pageSize);

    /**
     * POST /inventory/players/{playerId}/items/consume : Использовать потребляемый предмет
     *
     * @param playerId Идентификатор игрока/персонажа (required)
     * @param itemConsumeRequest  (required)
     * @return Void
     */
    Void inventoryPlayersPlayerIdItemsConsumePost(String playerId, ItemConsumeRequest itemConsumeRequest);

    /**
     * POST /inventory/players/{playerId}/items/drop : Сбросить предмет в мир
     *
     * @param playerId Идентификатор игрока/персонажа (required)
     * @param itemDropRequest  (required)
     * @return Void
     */
    Void inventoryPlayersPlayerIdItemsDropPost(String playerId, ItemDropRequest itemDropRequest);

    /**
     * POST /inventory/players/{playerId}/items/equip : Экипировать предмет
     *
     * @param playerId Идентификатор игрока/персонажа (required)
     * @param itemEquipRequest  (required)
     * @return Void
     */
    Void inventoryPlayersPlayerIdItemsEquipPost(String playerId, ItemEquipRequest itemEquipRequest);

    /**
     * POST /inventory/players/{playerId}/items/merge : Объединить стеки
     *
     * @param playerId Идентификатор игрока/персонажа (required)
     * @param itemMergeRequest  (required)
     * @return Void
     */
    Void inventoryPlayersPlayerIdItemsMergePost(String playerId, ItemMergeRequest itemMergeRequest);

    /**
     * POST /inventory/players/{playerId}/items/move : Переместить предмет
     *
     * @param playerId Идентификатор игрока/персонажа (required)
     * @param itemMoveRequest  (required)
     * @return Void
     */
    Void inventoryPlayersPlayerIdItemsMovePost(String playerId, ItemMoveRequest itemMoveRequest);

    /**
     * POST /inventory/players/{playerId}/items/pickup : Подобрать предмет и добавить в инвентарь
     *
     * @param playerId Идентификатор игрока/персонажа (required)
     * @param itemPickupRequest  (required)
     * @return ItemOperationResult
     */
    ItemOperationResult inventoryPlayersPlayerIdItemsPickupPost(String playerId, ItemPickupRequest itemPickupRequest);

    /**
     * POST /inventory/players/{playerId}/items/split : Разделить стек предметов
     *
     * @param playerId Идентификатор игрока/персонажа (required)
     * @param itemSplitRequest  (required)
     * @return Void
     */
    Void inventoryPlayersPlayerIdItemsSplitPost(String playerId, ItemSplitRequest itemSplitRequest);

    /**
     * POST /inventory/players/{playerId}/items/unequip : Снять предмет
     *
     * @param playerId Идентификатор игрока/персонажа (required)
     * @param itemUnequipRequest  (required)
     * @return Void
     */
    Void inventoryPlayersPlayerIdItemsUnequipPost(String playerId, ItemUnequipRequest itemUnequipRequest);

    /**
     * POST /inventory/players/{playerId}/release : Снять резерв предметов
     *
     * @param playerId Идентификатор игрока/персонажа (required)
     * @param reservationReleaseRequest  (required)
     * @return Void
     */
    Void inventoryPlayersPlayerIdReleasePost(String playerId, ReservationReleaseRequest reservationReleaseRequest);

    /**
     * POST /inventory/players/{playerId}/reserve : Зарезервировать предметы
     *
     * @param playerId Идентификатор игрока/персонажа (required)
     * @param reservationRequest  (required)
     * @return Reservation
     */
    Reservation inventoryPlayersPlayerIdReservePost(String playerId, ReservationRequest reservationRequest);

    /**
     * POST /inventory/players/{playerId}/stash/transfer : Перемещение между backpack и stash/bank
     *
     * @param playerId Идентификатор игрока/персонажа (required)
     * @param stashTransferRequest  (required)
     * @return Void
     */
    Void inventoryPlayersPlayerIdStashTransferPost(String playerId, StashTransferRequest stashTransferRequest);

    /**
     * GET /inventory/players/{playerId}/weight : Получить информацию о весе
     *
     * @param playerId Идентификатор игрока/персонажа (required)
     * @return WeightInfo
     */
    WeightInfo inventoryPlayersPlayerIdWeightGet(String playerId);

    /**
     * GET /inventory/templates/items/{itemId} : Получить метаданные предмета
     *
     * @param itemId Идентификатор шаблона предмета (required)
     * @return ItemTemplate
     */
    ItemTemplate inventoryTemplatesItemsItemIdGet(String itemId);
}

