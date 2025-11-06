package com.necpgame.backjava.service;

import com.necpgame.backjava.model.*;

import java.util.UUID;
import java.util.List;

/**
 * ImplantsLimitsService - сервис для управления ограничениями и энергетикой имплантов.
 * 
 * Сгенерировано из: API-SWAGGER/api/v1/gameplay/combat/implants-limits.yaml
 * 
 * НЕ редактируйте этот файл вручную - он генерируется автоматически!
 */
public interface ImplantsLimitsService {

    /**
     * Получить доступные слоты имплантов игрока.
     * 
     * @param playerId ID игрока
     * @param type фильтр по типу слота (опционально)
     * @return информация о слотах имплантов
     */
    ImplantSlots getImplantSlots(UUID playerId, String type);

    /**
     * Проверить совместимость импланта с текущими имплантами.
     * 
     * @param playerId ID игрока
     * @param request запрос на проверку совместимости
     * @return результат проверки совместимости
     */
    CompatibilityResult checkCompatibility(UUID playerId, CompatibilityCheckRequest request);

    /**
     * Получить все лимиты имплантов игрока.
     * 
     * @param playerId ID игрока
     * @return информация о всех лимитах
     */
    ImplantLimits getImplantLimits(UUID playerId);

    /**
     * Получить текущий лимит имплантов игрока.
     * 
     * @param playerId ID игрока
     * @return информация о текущем лимите
     */
    ImplantLimitInfo getImplantLimit(UUID playerId);

    /**
     * Рассчитать лимит имплантов с учетом модификаторов.
     * 
     * @param playerId ID игрока
     * @param request запрос на расчет лимита
     * @return результат расчета лимита
     */
    ImplantLimitCalculation calculateImplantLimit(UUID playerId, CalculateLimitRequest request);

    /**
     * Получить энергетический пул игрока.
     * 
     * @param playerId ID игрока
     * @return информация об энергетическом пуле
     */
    EnergyPoolInfo getEnergyPool(UUID playerId);

    /**
     * Рассчитать энергетическое потребление имплантов.
     * 
     * @param playerId ID игрока
     * @param request запрос на расчет энергопотребления
     * @return результат расчета энергопотребления
     */
    EnergyCalculation calculateEnergyConsumption(UUID playerId, CalculateEnergyRequest request);

    /**
     * Восстановить энергию.
     * 
     * @param playerId ID игрока
     * @param request запрос на восстановление энергии
     * @return результат восстановления энергии
     */
    EnergyRestoreResult restoreEnergy(UUID playerId, RestoreEnergyRequest request);

    /**
     * Получить индивидуальные энергетические лимиты имплантов.
     * 
     * @param playerId ID игрока
     * @return список индивидуальных лимитов имплантов
     */
    List<IndividualEnergyLimits> getIndividualEnergyLimits(UUID playerId);

    /**
     * Валидировать установку импланта.
     * 
     * @param playerId ID игрока
     * @param request запрос на валидацию установки
     * @return результат валидации
     */
    ValidationResult validateInstall(UUID playerId, ValidateInstallRequest request);
}

