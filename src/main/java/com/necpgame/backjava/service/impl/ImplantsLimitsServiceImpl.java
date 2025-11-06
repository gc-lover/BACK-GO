package com.necpgame.backjava.service.impl;

import com.necpgame.backjava.entity.*;
import com.necpgame.backjava.exception.BusinessException;
import com.necpgame.backjava.exception.ErrorCode;
import com.necpgame.backjava.model.*;
import com.necpgame.backjava.repository.*;
import com.necpgame.backjava.service.ImplantsLimitsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для управления ограничениями и энергетикой имплантов.
 * 
 * Источник: API-SWAGGER/api/v1/gameplay/combat/implants-limits.yaml
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImplantsLimitsServiceImpl implements ImplantsLimitsService {
    
    private final CharacterRepository characterRepository;
    private final CharacterImplantStatsRepository implantStatsRepository;
    private final CharacterImplantSlotRepository implantSlotRepository;
    private final CharacterImplantRepository characterImplantRepository;
    private final ImplantRepository implantRepository;
    
    @Override
    @Transactional(readOnly = true)
    public ImplantSlots getImplantSlots(UUID playerId, String type) {
        log.info("Getting implant slots for player: {}, type: {}", playerId, type);
        
        // Получаем персонажа
        CharacterEntity character = characterRepository.findById(playerId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                "Character not found: " + playerId));
        
        // Получаем слоты персонажа
        List<CharacterImplantSlotEntity> slots = implantSlotRepository.findByCharacterId(playerId);
        
        // Создаем слоты по умолчанию если нет
        if (slots.isEmpty()) {
            slots = createDefaultSlots(character);
        }
        
        // Преобразуем в DTO
        ImplantSlots result = new ImplantSlots();
        ImplantSlotsSlotsByType slotsByType = new ImplantSlotsSlotsByType();
        
        for (CharacterImplantSlotEntity slot : slots) {
            if (type == null || slot.getSlotType().name().equals(type)) {
                SlotInfo slotInfo = new SlotInfo();
                slotInfo.setMax(slot.getMaxSlots());
                slotInfo.setUsed(slot.getUsedSlots());
                slotInfo.setAvailable(slot.getMaxSlots() + slot.getBonusSlots() - slot.getUsedSlots());
                slotInfo.setBonus(slot.getBonusSlots());
                
                // Добавляем в результат (используем рефлексию для динамического добавления)
                // В реальности нужно добавить методы в ImplantSlotsSlotsByType для каждого типа
            }
        }
        
        result.setSlotsByType(slotsByType);
        return result;
    }
    
    @Override
    @Transactional(readOnly = true)
    public CompatibilityResult checkCompatibility(UUID playerId, CompatibilityCheckRequest request) {
        log.info("Checking compatibility for player: {}, implant: {}", playerId, request.getImplantId());
        
        // Получаем имплант
        ImplantEntity implant = implantRepository.findById(request.getImplantId())
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                "Implant not found: " + request.getImplantId()));
        
        // Получаем установленные импланты
        List<CharacterImplantEntity> installedImplants = characterImplantRepository
            .findActiveByCharacterId(playerId);
        
        // Проверяем совместимость
        boolean compatible = true;
        List<Conflict> conflicts = new ArrayList<>();
        List<com.necpgame.backjava.model.Warning> warnings = new ArrayList<>();
        
        // TODO: Реализовать логику проверки совместимости
        
        CompatibilityResult result = new CompatibilityResult();
        result.setCompatible(compatible);
        result.setConflicts(conflicts);
        result.setWarnings(warnings);
        result.setRecommendations(new ArrayList<>());
        
        return result;
    }
    
    @Override
    @Transactional(readOnly = true)
    public ImplantLimits getImplantLimits(UUID playerId) {
        log.info("Getting implant limits for player: {}", playerId);
        
        CharacterImplantStatsEntity stats = getOrCreateStats(playerId);
        
        ImplantLimits result = new ImplantLimits();
        result.setTotalLimit(stats.getCurrentImplantLimit());
        result.setUsed(stats.getUsedImplants());
        result.setAvailable(stats.getCurrentImplantLimit() - stats.getUsedImplants());
        
        return result;
    }
    
    @Override
    @Transactional(readOnly = true)
    public ImplantLimitInfo getImplantLimit(UUID playerId) {
        log.info("Getting implant limit info for player: {}", playerId);
        
        CharacterImplantStatsEntity stats = getOrCreateStats(playerId);
        
        ImplantLimitInfo info = new ImplantLimitInfo();
        info.setBaseLimit(stats.getBaseImplantLimit());
        info.setCurrentLimit(stats.getCurrentImplantLimit());
        info.setUsed(stats.getUsedImplants());
        info.setAvailable(stats.getCurrentImplantLimit() - stats.getUsedImplants());
        info.setHumanityPenalty(stats.getHumanityPenalty());
        info.setCanExceedTemporarily(stats.getCanExceedLimitTemporarily());
        
        ImplantLimitInfoBonuses bonuses = new ImplantLimitInfoBonuses();
        bonuses.setFromStats(stats.getImplantLimitBonus());
        bonuses.setFromPerks(0);
        bonuses.setFromItems(0);
        bonuses.setTotal(stats.getImplantLimitBonus());
        info.setBonuses(bonuses);
        
        return info;
    }
    
    @Override
    @Transactional(readOnly = true)
    public ImplantLimitCalculation calculateImplantLimit(UUID playerId, CalculateLimitRequest request) {
        log.info("Calculating implant limit for player: {}", playerId);
        
        // TODO: Реализовать полную логику расчета
        
        ImplantLimitCalculation calculation = new ImplantLimitCalculation();
        calculation.setBaseLimit(10);
        calculation.setFinalLimit(10);
        
        return calculation;
    }
    
    @Override
    @Transactional(readOnly = true)
    public EnergyPoolInfo getEnergyPool(UUID playerId) {
        log.info("Getting energy pool for player: {}", playerId);
        
        CharacterImplantStatsEntity stats = getOrCreateStats(playerId);
        
        EnergyPoolInfo info = new EnergyPoolInfo();
        info.setTotalPool(stats.getTotalEnergyPool());
        info.setUsed(stats.getUsedEnergy());
        info.setAvailable(stats.getAvailableEnergy());
        info.setRegenRate(stats.getEnergyRegenRate());
        info.setCurrentLevel(stats.getCurrentEnergyLevel());
        info.setMaxLevel(stats.getMaxEnergyLevel());
        info.setIndividualLimits(new ArrayList<>());
        
        return info;
    }
    
    @Override
    @Transactional(readOnly = true)
    public EnergyCalculation calculateEnergyConsumption(UUID playerId, CalculateEnergyRequest request) {
        log.info("Calculating energy consumption for player: {}", playerId);
        
        // TODO: Реализовать полную логику расчета энергопотребления
        
        EnergyCalculation calculation = new EnergyCalculation();
        calculation.setTotalConsumption(0.0f);
        calculation.setCanActivateAll(true);
        calculation.setExceedsPool(false);
        
        return calculation;
    }
    
    @Override
    @Transactional
    public EnergyRestoreResult restoreEnergy(UUID playerId, RestoreEnergyRequest request) {
        log.info("Restoring energy for player: {}, amount: {}", playerId, request.getAmount());
        
        CharacterImplantStatsEntity stats = getOrCreateStats(playerId);
        
        Float restored = Math.min(request.getAmount(), stats.getTotalEnergyPool() - stats.getCurrentEnergyLevel());
        stats.setCurrentEnergyLevel(stats.getCurrentEnergyLevel() + restored);
        stats.setAvailableEnergy(stats.getTotalEnergyPool() - stats.getUsedEnergy());
        
        implantStatsRepository.save(stats);
        
        EnergyRestoreResult result = new EnergyRestoreResult();
        result.setRestored(restored);
        result.setNewLevel(stats.getCurrentEnergyLevel());
        result.setAvailable(stats.getAvailableEnergy());
        
        return result;
    }
    
    @Override
    @Transactional(readOnly = true)
    public IndividualEnergyLimits getIndividualEnergyLimits(UUID playerId, Boolean activeOnly) {
        log.info("Getting individual energy limits for player: {}, activeOnly: {}", playerId, activeOnly);
        
        // TODO: Реализовать получение индивидуальных лимитов имплантов
        
        IndividualEnergyLimits limits = new IndividualEnergyLimits();
        limits.setLimits(new ArrayList<>());
        
        return limits;
    }
    
    @Override
    @Transactional(readOnly = true)
    public ValidationResult validateInstall(UUID playerId, ValidateInstallRequest request) {
        log.info("Validating implant install for player: {}, implant: {}", playerId, request.getImplantId());
        
        // Проверяем существование персонажа
        characterRepository.findById(playerId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.RESOURCE_NOT_FOUND, 
                "Character not found: " + playerId));
        
        // Проверяем существование импланта
        ImplantEntity implant = implantRepository.findById(request.getImplantId())
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                "Implant not found: " + request.getImplantId()));
        
        boolean valid = true;
        List<com.necpgame.backjava.model.Reason> reasons = new ArrayList<>();
        List<com.necpgame.backjava.model.Warning> warnings = new ArrayList<>();
        
        // TODO: Реализовать логику валидации (слоты, энергия, совместимость)
        
        ValidationResult result = new ValidationResult();
        result.setValid(valid);
        result.setReasons(reasons);
        result.setWarnings(warnings);
        
        return result;
    }
    
    // ===== Helper methods =====
    
    private CharacterImplantStatsEntity getOrCreateStats(UUID characterId) {
        return implantStatsRepository.findByCharacterId(characterId)
            .orElseGet(() -> {
                CharacterEntity character = characterRepository.findById(characterId)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.RESOURCE_NOT_FOUND, 
                        "Character not found: " + characterId));
                
                CharacterImplantStatsEntity stats = new CharacterImplantStatsEntity();
                stats.setCharacter(character);
                stats.setBaseImplantLimit(10);
                stats.setCurrentImplantLimit(10);
                stats.setUsedImplants(0);
                stats.setTotalEnergyPool(100.0f);
                stats.setUsedEnergy(0.0f);
                stats.setAvailableEnergy(100.0f);
                stats.setEnergyRegenRate(1.0f);
                stats.setCurrentEnergyLevel(100.0f);
                stats.setMaxEnergyLevel(100.0f);
                
                return implantStatsRepository.save(stats);
            });
    }
    
    private List<CharacterImplantSlotEntity> createDefaultSlots(CharacterEntity character) {
        List<CharacterImplantSlotEntity> slots = new ArrayList<>();
        
        // Создаем слоты по умолчанию для каждого типа
        for (CharacterImplantSlotEntity.SlotType slotType : CharacterImplantSlotEntity.SlotType.values()) {
            CharacterImplantSlotEntity slot = new CharacterImplantSlotEntity();
            slot.setCharacter(character);
            slot.setSlotType(slotType);
            slot.setMaxSlots(2); // По умолчанию 2 слота каждого типа
            slot.setUsedSlots(0);
            slot.setBonusSlots(0);
            slots.add(slot);
        }
        
        return implantSlotRepository.saveAll(slots);
    }
}

