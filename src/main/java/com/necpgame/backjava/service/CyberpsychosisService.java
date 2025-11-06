package com.necpgame.backjava.service;

import com.necpgame.backjava.model.*;

import java.util.UUID;
import java.util.List;

/**
 * CyberpsychosisService - сервис для управления системой киберпсихоза.
 * 
 * Сгенерировано из: API-SWAGGER/api/v1/gameplay/combat/cyberpsychosis.yaml
 * 
 * НЕ редактируйте этот файл вручную - он генерируется автоматически!
 */
public interface CyberpsychosisService {

    // ===== Управление человечностью =====
    
    /**
     * Получить текущий уровень человечности игрока.
     */
    HumanityInfo getHumanity(UUID playerId);
    
    /**
     * Рассчитать потерю человечности от импланта.
     */
    HumanityLossCalculation calculateHumanityLoss(UUID playerId, CalculateLossRequest request);
    
    /**
     * Применить потерю человечности.
     */
    HumanityUpdateResult applyHumanityLoss(UUID playerId, ApplyLossRequest request);
    
    // ===== Стадии киберпсихоза =====
    
    /**
     * Получить текущую стадию киберпсихоза.
     */
    CyberpsychosisStage getCyberpsychosisStage(UUID playerId);
    
    /**
     * Получить список симптомов текущей стадии.
     */
    List<Symptom> getSymptoms(UUID playerId, Boolean activeOnly, String severity);
    
    /**
     * Получить информацию о конкретной стадии.
     */
    StageInfo getStageInfo(String stageId);
    
    // ===== Прогрессия киберпсихоза =====
    
    /**
     * Получить информацию о прогрессии киберпсихоза.
     */
    ProgressionInfo getProgression(UUID playerId);
    
    /**
     * Рассчитать риск прогрессии.
     */
    ProgressionCalculation calculateProgression(UUID playerId, CalculateProgressionRequest request);
    
    /**
     * Триггернуть прогрессию киберпсихоза.
     */
    ProgressionTriggerResult triggerProgression(UUID playerId, TriggerProgressionRequest request);
    
    // ===== Последствия киберпсихоза =====
    
    /**
     * Получить все последствия киберпсихоза.
     */
    ConsequencesInfo getConsequences(UUID playerId);
    
    /**
     * Получить штрафы к характеристикам.
     */
    StatPenalties getStatPenalties(UUID playerId, Boolean includeTemporary);
    
    /**
     * Получить социальные эффекты киберпсихоза.
     */
    SocialEffects getSocialEffects(UUID playerId);
    
    // ===== Управление киберпсихозом =====
    
    /**
     * Применить профилактику киберпсихоза.
     */
    PreventionResult applyPrevention(UUID playerId, ApplyPreventionRequest request);
    
    /**
     * Применить лечение киберпсихоза.
     */
    TreatmentResult applyTreatment(UUID playerId, ApplyTreatmentRequest request);
    
    /**
     * Получить доступные методы лечения.
     */
    GetTreatments200Response getTreatments(UUID playerId, String type, Integer page, Integer pageSize);
    
    /**
     * Применить управление симптомами.
     */
    SymptomManagementResult applySymptomManagement(UUID playerId, ApplySymptomManagementRequest request);
    
    /**
     * Получить информацию об адаптации к киберпсихозу.
     */
    AdaptationInfo getAdaptation(UUID playerId);
    
    /**
     * Удалить имплант для восстановления человечности.
     */
    ImplantRemovalResult removeImplant(UUID playerId, RemoveImplantRequest request);
    
    /**
     * Выполнить детоксикацию.
     */
    DetoxificationResult performDetoxification(UUID playerId, DetoxificationRequest request);
    
    /**
     * Получить стоимость лечения.
     */
    TreatmentCosts getTreatmentCosts(UUID playerId, String treatmentType);
    
    /**
     * Применить социальную поддержку для снижения киберпсихоза.
     */
    SocialSupportResult applySocialSupport(UUID playerId, ApplySocialSupportRequest request);
}

