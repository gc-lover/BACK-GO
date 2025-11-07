package com.necpgame.backjava.service.impl;

import com.necpgame.backjava.entity.AccountEntity;
import com.necpgame.backjava.entity.CharacterEntity;
import com.necpgame.backjava.entity.CharacterEquipmentEntity;
import com.necpgame.backjava.entity.CharacterInventoryEntity;
import com.necpgame.backjava.entity.CharacterInventoryEntity.BindType;
import com.necpgame.backjava.entity.CharacterInventoryEntity.StorageType;
import com.necpgame.backjava.entity.InventoryItemEntity;
import com.necpgame.backjava.entity.PlayerBankSlotEntity;
import com.necpgame.backjava.entity.PlayerEntity;
import com.necpgame.backjava.exception.BusinessException;
import com.necpgame.backjava.exception.ErrorCode;
import com.necpgame.backjava.model.CharacterInventory;
import com.necpgame.backjava.model.DropItemRequest;
import com.necpgame.backjava.model.EquipItemRequest;
import com.necpgame.backjava.model.InventoryItem;
import com.necpgame.backjava.model.PickupItem200Response;
import com.necpgame.backjava.model.PickupItemRequest;
import com.necpgame.backjava.model.UseItemRequest;
import com.necpgame.backjava.repository.AccountRepository;
import com.necpgame.backjava.repository.CharacterEquipmentRepository;
import com.necpgame.backjava.repository.CharacterInventoryRepository;
import com.necpgame.backjava.repository.CharacterRepository;
import com.necpgame.backjava.repository.InventoryItemRepository;
import com.necpgame.backjava.repository.PlayerBankSlotRepository;
import com.necpgame.backjava.repository.PlayerRepository;
import com.necpgame.backjava.service.InventoryService;
import com.necpgame.backjava.util.SecurityUtil;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private static final int BACKPACK_SLOT_CAPACITY = 50;
    private static final int BANK_SLOT_CAPACITY = 100;
    private static final BigDecimal BACKPACK_WEIGHT_LIMIT = BigDecimal.valueOf(100);

    private final PlayerRepository playerRepository;
    private final AccountRepository accountRepository;
    private final CharacterRepository characterRepository;
    private final CharacterInventoryRepository characterInventoryRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final CharacterEquipmentRepository characterEquipmentRepository;
    private final PlayerBankSlotRepository playerBankSlotRepository;

    @Override
    @Transactional(readOnly = true)
    public CharacterInventory getInventory(String characterId) {
        UUID accountId = SecurityUtil.getCurrentAccountId();
        UUID characterUuid = parseUuid(characterId, "character_id");
        CharacterEntity character = loadCharacter(characterUuid, accountId);

        List<CharacterInventoryEntity> backpackItems = characterInventoryRepository
            .findByCharacterIdAndStorageTypeOrderBySlotPosition(character.getId(), StorageType.BACKPACK);

        Map<String, InventoryItemEntity> templateCache = new HashMap<>();
        Set<String> equippedItemIds = characterEquipmentRepository.findByCharacterId(character.getId()).stream()
            .map(CharacterEquipmentEntity::getItemId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        BigDecimal currentWeight = characterInventoryRepository.calculateTotalWeight(
            character.getId(),
            List.of(StorageType.BACKPACK, StorageType.EQUIPPED)
        );

        CharacterInventory dto = new CharacterInventory();
        dto.setCharacterId(characterId);
        dto.setSlotsTotal(BACKPACK_SLOT_CAPACITY);
        dto.setSlotsUsed(backpackItems.size());
        dto.setWeightCurrent(currentWeight);
        dto.setWeightMax(BACKPACK_WEIGHT_LIMIT);
        dto.setIsOverencumbered(currentWeight.compareTo(BACKPACK_WEIGHT_LIMIT) > 0);
        dto.setItems(
            backpackItems.stream()
                .map(entry -> toInventoryItem(entry, templateCache, equippedItemIds.contains(entry.getItemId())))
                .collect(Collectors.toList())
        );
        return dto;
    }

    @Override
    public PickupItem200Response pickupItem(String characterId, PickupItemRequest request) {
        UUID accountId = SecurityUtil.getCurrentAccountId();
        UUID characterUuid = parseUuid(characterId, "character_id");
        CharacterEntity character = loadCharacter(characterUuid, accountId);

        String itemId = Optional.ofNullable(request.getItemId())
            .orElseThrow(() -> new BusinessException(ErrorCode.MISSING_REQUIRED_FIELD, "Не указан item_id"));
        InventoryItemEntity template = resolveItemTemplate(itemId);

        int quantity = Optional.ofNullable(request.getQuantity()).orElse(1);
        if (quantity <= 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "Количество должно быть положительным");
        }

        BigDecimal additionalWeight = template.getWeight().multiply(BigDecimal.valueOf(quantity));
        BigDecimal currentWeight = characterInventoryRepository.calculateTotalWeight(
            character.getId(), List.of(StorageType.BACKPACK, StorageType.EQUIPPED)
        );
        if (currentWeight.add(additionalWeight).compareTo(BACKPACK_WEIGHT_LIMIT) > 0) {
            throw new BusinessException(ErrorCode.LIMIT_EXCEEDED, "Превышен лимит веса");
        }

        List<CharacterInventoryEntity> existingStacks = characterInventoryRepository
            .findByCharacterIdAndItemIdAndStorageType(character.getId(), itemId, StorageType.BACKPACK);
        existingStacks.sort(Comparator.comparing(CharacterInventoryEntity::getSlotPosition, Comparator.nullsLast(Integer::compareTo)));

        Set<Integer> occupiedSlots = characterInventoryRepository
            .findByCharacterIdAndStorageTypeOrderBySlotPosition(character.getId(), StorageType.BACKPACK)
            .stream()
            .map(CharacterInventoryEntity::getSlotPosition)
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(HashSet::new));

        PickupItem200Response response = new PickupItem200Response()
            .success(true)
            .itemId(itemId);

        int remaining = quantity;
        Integer maxStack = template.getMaxStackSize() != null && template.getMaxStackSize() > 0
            ? template.getMaxStackSize()
            : Integer.MAX_VALUE;

        if (template.getStackable() && !existingStacks.isEmpty()) {
            CharacterInventoryEntity stack = existingStacks.get(0);
            int available = maxStack - stack.getQuantity();
            if (available > 0) {
                int toAdd = Math.min(remaining, available);
                stack.setQuantity(stack.getQuantity() + toAdd);
                initialiseDurabilityIfNeeded(stack, template);
                characterInventoryRepository.save(stack);
                remaining -= toAdd;
                response.slot(stack.getSlotPosition());
            }
        }

        while (remaining > 0) {
            if (occupiedSlots.size() >= BACKPACK_SLOT_CAPACITY) {
                throw new BusinessException(ErrorCode.LIMIT_EXCEEDED, "Нет свободных слотов инвентаря");
            }
            int slot = findFirstFreeSlot(occupiedSlots);
            int toStore = template.getStackable() ? Math.min(remaining, maxStack) : 1;

            CharacterInventoryEntity entry = new CharacterInventoryEntity();
            entry.setCharacterId(character.getId());
            entry.setItemId(itemId);
            entry.setQuantity(toStore);
            entry.setSlotPosition(slot);
            entry.setStorageType(StorageType.BACKPACK);
            initialiseDurabilityIfNeeded(entry, template);
            applyBindingOnPickup(entry, template);

            entry = characterInventoryRepository.save(entry);
            response.slot(slot);
            occupiedSlots.add(slot);
            remaining -= toStore;
        }

        return response;
    }

    @Override
    public Object dropItem(String characterId, DropItemRequest request) {
        UUID accountId = SecurityUtil.getCurrentAccountId();
        UUID characterUuid = parseUuid(characterId, "character_id");
        CharacterEntity character = loadCharacter(characterUuid, accountId);

        String itemId = Optional.ofNullable(request.getItemId())
            .orElseThrow(() -> new BusinessException(ErrorCode.MISSING_REQUIRED_FIELD, "Не указан item_id"));
        int quantity = Optional.ofNullable(request.getQuantity()).orElse(1);
        if (quantity <= 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "Количество должно быть положительным");
        }

        List<CharacterInventoryEntity> stacks = characterInventoryRepository
            .findByCharacterIdAndItemIdAndStorageType(character.getId(), itemId, StorageType.BACKPACK)
            .stream()
            .sorted(Comparator.comparing(CharacterInventoryEntity::getSlotPosition, Comparator.nullsLast(Integer::compareTo)))
            .collect(Collectors.toList());
        if (stacks.isEmpty()) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Предмет не найден в инвентаре");
        }

        int remaining = quantity;
        for (CharacterInventoryEntity stack : stacks) {
            if (remaining <= 0) {
                break;
            }
            if (stack.getQuantity() > remaining) {
                stack.setQuantity(stack.getQuantity() - remaining);
                characterInventoryRepository.save(stack);
                remaining = 0;
            } else {
                remaining -= stack.getQuantity();
                characterInventoryRepository.delete(stack);
            }
        }

        if (remaining > 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "Недостаточное количество предметов для удаления");
        }

        return Map.of("success", Boolean.TRUE);
    }

    @Override
    public Object equipItem(String characterId, EquipItemRequest request) {
        UUID accountId = SecurityUtil.getCurrentAccountId();
        UUID characterUuid = parseUuid(characterId, "character_id");
        CharacterEntity character = loadCharacter(characterUuid, accountId);

        String itemId = Optional.ofNullable(request.getItemId())
            .orElseThrow(() -> new BusinessException(ErrorCode.MISSING_REQUIRED_FIELD, "Не указан item_id"));
        InventoryItemEntity template = resolveItemTemplate(itemId);
        if (!Boolean.TRUE.equals(template.getEquippable())) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "Предмет нельзя экипировать");
        }

        CharacterEquipmentEntity.SlotType slotType = resolveSlotType(request.getSlotType());

        List<CharacterInventoryEntity> backpackItems = characterInventoryRepository
            .findByCharacterIdAndItemIdAndStorageType(character.getId(), itemId, StorageType.BACKPACK)
            .stream()
            .sorted(Comparator.comparing(CharacterInventoryEntity::getSlotPosition, Comparator.nullsLast(Integer::compareTo)))
            .collect(Collectors.toList());
        if (backpackItems.isEmpty()) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Предмет отсутствует в рюкзаке");
        }

        CharacterInventoryEntity sourceEntry = backpackItems.get(0);
        CharacterInventoryEntity equippedEntry = detachFromBackpack(sourceEntry, template, backpackItems);

        CharacterEquipmentEntity equipment = characterEquipmentRepository
            .findByCharacterIdAndSlotType(character.getId(), slotType)
            .orElseGet(() -> {
                CharacterEquipmentEntity entity = new CharacterEquipmentEntity();
                entity.setCharacterId(character.getId());
                entity.setSlotType(slotType);
                return entity;
            });

        if (equipment.getInventoryItemId() != null && !equipment.getInventoryItemId().equals(equippedEntry.getId())) {
            unequipCurrentItem(character, equipment);
        }

        equipment.setItemId(itemId);
        equipment.setInventoryItemId(equippedEntry.getId());
        initialiseDurabilityIfNeeded(equippedEntry, template);
        applyBindingOnEquip(equippedEntry, template);

        characterInventoryRepository.save(equippedEntry);
        characterEquipmentRepository.save(equipment);

        return Map.of(
            "success", Boolean.TRUE,
            "slot_type", slotType.name().toLowerCase()
        );
    }

    @Override
    public Object useItem(String characterId, UseItemRequest request) {
        UUID accountId = SecurityUtil.getCurrentAccountId();
        UUID characterUuid = parseUuid(characterId, "character_id");
        CharacterEntity character = loadCharacter(characterUuid, accountId);

        String itemId = Optional.ofNullable(request.getItemId())
            .orElseThrow(() -> new BusinessException(ErrorCode.MISSING_REQUIRED_FIELD, "Не указан item_id"));
        InventoryItemEntity template = resolveItemTemplate(itemId);
        if (!Boolean.TRUE.equals(template.getUsable())) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "Предмет нельзя использовать");
        }

        int quantity = Optional.ofNullable(request.getQuantity()).orElse(1);
        if (quantity <= 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "Количество должно быть положительным");
        }

        List<CharacterInventoryEntity> stacks = characterInventoryRepository
            .findByCharacterIdAndItemIdAndStorageType(character.getId(), itemId, StorageType.BACKPACK)
            .stream()
            .sorted(Comparator.comparing(CharacterInventoryEntity::getSlotPosition, Comparator.nullsLast(Integer::compareTo)))
            .collect(Collectors.toList());
        if (stacks.isEmpty()) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Предмет отсутствует в рюкзаке");
        }

        int remaining = quantity;
        for (CharacterInventoryEntity stack : stacks) {
            if (remaining <= 0) {
                break;
            }
            if (stack.getQuantity() > remaining) {
                stack.setQuantity(stack.getQuantity() - remaining);
                characterInventoryRepository.save(stack);
                remaining = 0;
            } else {
                remaining -= stack.getQuantity();
                characterInventoryRepository.delete(stack);
            }
        }

        if (remaining > 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "Недостаточное количество предметов");
        }

        return Map.of("success", Boolean.TRUE);
    }

    @Override
    @Transactional(readOnly = true)
    public Object getEquipment(String characterId) {
        UUID accountId = SecurityUtil.getCurrentAccountId();
        UUID characterUuid = parseUuid(characterId, "character_id");
        CharacterEntity character = loadCharacter(characterUuid, accountId);

        List<CharacterEquipmentEntity> equipment = characterEquipmentRepository.findByCharacterId(character.getId());
        Map<String, InventoryItemEntity> templateCache = new HashMap<>();

        List<Map<String, Object>> slots = new ArrayList<>();
        for (CharacterEquipmentEntity slot : equipment) {
            Map<String, Object> slotDto = new HashMap<>();
            slotDto.put("slot_type", slot.getSlotType().name().toLowerCase());

            if (slot.getInventoryItemId() != null) {
                CharacterInventoryEntity entry = characterInventoryRepository
                    .findByIdAndCharacterId(slot.getInventoryItemId(), character.getId())
                    .orElse(null);
                if (entry != null) {
                    InventoryItem itemDto = toInventoryItem(entry, templateCache, true);
                    itemDto.setSlot(null);
                    slotDto.put("item", itemDto);
                }
            }

            slots.add(slotDto);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("character_id", characterId);
        response.put("slots", slots);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Object getBankStorage(String playerId) {
        UUID accountId = SecurityUtil.getCurrentAccountId();
        UUID playerUuid = parseUuid(playerId, "player_id");
        PlayerEntity player = loadPlayer(playerUuid, accountId);

        List<PlayerBankSlotEntity> slots = playerBankSlotRepository.findByPlayerIdOrderBySlotIndex(player.getId());
        Map<String, InventoryItemEntity> templateCache = new HashMap<>();
        List<Map<String, Object>> items = new ArrayList<>();

        for (PlayerBankSlotEntity slot : slots) {
            if (slot.getItemId() == null) {
                continue;
            }
            InventoryItemEntity template = templateCache.computeIfAbsent(
                slot.getItemId(), inventoryItemRepository::findById
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Шаблон предмета не найден")));

            Map<String, Object> item = new HashMap<>();
            item.put("slot", slot.getSlotIndex());
            item.put("item_id", slot.getItemId());
            item.put("name", template.getName());
            item.put("stack_size", slot.getQuantity());
            item.put("durability", slot.getCurrentDurability());
            item.put("is_bound", slot.getBound());
            items.add(item);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("player_id", playerId);
        response.put("slots_total", BANK_SLOT_CAPACITY);
        response.put("slots_used", items.size());
        response.put("items", items);
        return response;
    }

    private CharacterEntity loadCharacter(UUID characterId, UUID accountId) {
        return characterRepository.findByIdWithDetails(characterId)
            .filter(character -> character.getAccount() != null && character.getAccount().getId().equals(accountId))
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Персонаж не найден или не принадлежит аккаунту"));
    }

    private PlayerEntity loadPlayer(UUID playerId, UUID accountId) {
        PlayerEntity player = playerRepository.findById(playerId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Игрок не найден"));
        if (player.getAccount() == null || !player.getAccount().getId().equals(accountId)) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "Игрок принадлежит другому аккаунту");
        }
        return player;
    }

    private InventoryItemEntity resolveItemTemplate(String itemId) {
        return inventoryItemRepository.findById(itemId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Шаблон предмета не найден"));
    }

    private InventoryItem toInventoryItem(
        CharacterInventoryEntity entry,
        Map<String, InventoryItemEntity> templateCache,
        boolean equipped
    ) {
        InventoryItemEntity template = templateCache.computeIfAbsent(
            entry.getItemId(),
            id -> inventoryItemRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Шаблон предмета не найден"))
        );

        InventoryItem dto = new InventoryItem();
        dto.setItemId(entry.getItemId());
        dto.setName(template.getName());
        dto.setSlot(entry.getSlotPosition());
        dto.setStackSize(entry.getQuantity());
        dto.setDurability(entry.getCurrentDurability());
        dto.setIsEquipped(equipped || entry.getStorageType() == StorageType.EQUIPPED);
        dto.setIsBound(entry.getBound());
        return dto;
    }

    private void initialiseDurabilityIfNeeded(CharacterInventoryEntity entry, InventoryItemEntity template) {
        if (Boolean.TRUE.equals(template.getHasDurability())) {
            int base = Optional.ofNullable(template.getBaseDurability()).orElse(100);
            entry.setMaxDurability(base);
            if (entry.getCurrentDurability() == null) {
                entry.setCurrentDurability(base);
            }
        }
    }

    private void applyBindingOnPickup(CharacterInventoryEntity entry, InventoryItemEntity template) {
        if (Boolean.TRUE.equals(template.getBindOnPickup())) {
            entry.setBound(true);
            entry.setBindType(BindType.PICKUP);
            entry.setBoundAt(OffsetDateTime.now());
        }
    }

    private void applyBindingOnEquip(CharacterInventoryEntity entry, InventoryItemEntity template) {
        if (!Boolean.TRUE.equals(entry.getBound()) && Boolean.TRUE.equals(template.getBindOnEquip())) {
            entry.setBound(true);
            entry.setBindType(BindType.EQUIP);
            entry.setBoundAt(OffsetDateTime.now());
        }
    }

    private CharacterInventoryEntity detachFromBackpack(
        CharacterInventoryEntity source,
        InventoryItemEntity template,
        List<CharacterInventoryEntity> allStacks
    ) {
        if (source.getQuantity() > 1 && Boolean.TRUE.equals(template.getStackable())) {
            source.setQuantity(source.getQuantity() - 1);
            characterInventoryRepository.save(source);

            CharacterInventoryEntity equippedEntry = new CharacterInventoryEntity();
            equippedEntry.setCharacterId(source.getCharacterId());
            equippedEntry.setItemId(source.getItemId());
            equippedEntry.setQuantity(1);
            equippedEntry.setStorageType(StorageType.EQUIPPED);
            equippedEntry.setCurrentDurability(source.getCurrentDurability());
            equippedEntry.setMaxDurability(source.getMaxDurability());
            equippedEntry.setBound(source.getBound());
            equippedEntry.setBindType(source.getBindType());
            equippedEntry.setBoundAt(source.getBoundAt());
            return equippedEntry;
        }

        source.setStorageType(StorageType.EQUIPPED);
        source.setSlotPosition(null);
        return source;
    }

    private void unequipCurrentItem(CharacterEntity character, CharacterEquipmentEntity equipment) {
        if (equipment.getInventoryItemId() == null) {
            return;
        }

        CharacterInventoryEntity entry = characterInventoryRepository
            .findByIdAndCharacterId(equipment.getInventoryItemId(), character.getId())
            .orElse(null);
        if (entry == null) {
            return;
        }

        Set<Integer> occupiedSlots = characterInventoryRepository
            .findByCharacterIdAndStorageTypeOrderBySlotPosition(character.getId(), StorageType.BACKPACK)
            .stream()
            .map(CharacterInventoryEntity::getSlotPosition)
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(HashSet::new));

        if (occupiedSlots.size() >= BACKPACK_SLOT_CAPACITY) {
            throw new BusinessException(ErrorCode.LIMIT_EXCEEDED, "Нет свободных слотов для перемещения экипировки в рюкзак");
        }

        int slot = findFirstFreeSlot(occupiedSlots);
        entry.setStorageType(StorageType.BACKPACK);
        entry.setSlotPosition(slot);
        characterInventoryRepository.save(entry);
    }

    private CharacterEquipmentEntity.SlotType resolveSlotType(String slotType) {
        if (slotType == null || slotType.isBlank()) {
            throw new BusinessException(ErrorCode.MISSING_REQUIRED_FIELD, "Не указан slot_type");
        }
        String normalized = slotType.trim().toUpperCase().replace('-', '_');
        try {
            return CharacterEquipmentEntity.SlotType.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "Недопустимый тип слота экипировки: " + slotType);
        }
    }

    private UUID parseUuid(String value, String field) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(ErrorCode.INVALID_FORMAT, "Некорректный формат " + field);
        }
    }

    private int findFirstFreeSlot(Set<Integer> occupiedSlots) {
        for (int i = 0; i < BACKPACK_SLOT_CAPACITY; i++) {
            if (!occupiedSlots.contains(i)) {
                return i;
            }
        }
        return -1;
    }
}

