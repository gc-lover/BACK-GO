package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.CyberpsychosisSymptomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository для управления симптомами киберпсихоза (справочник).
 * 
 * Источник: API-SWAGGER/api/v1/gameplay/combat/cyberpsychosis.yaml
 */
@Repository
public interface CyberpsychosisSymptomRepository extends JpaRepository<CyberpsychosisSymptomEntity, String> {
    
    /**
     * Найти симптомы по стадии.
     */
    List<CyberpsychosisSymptomEntity> findByStage(CyberpsychosisSymptomEntity.Stage stage);
    
    /**
     * Найти симптомы по серьезности.
     */
    List<CyberpsychosisSymptomEntity> findBySeverity(CyberpsychosisSymptomEntity.Severity severity);
    
    /**
     * Найти симптомы по категории.
     */
    List<CyberpsychosisSymptomEntity> findByCategory(CyberpsychosisSymptomEntity.Category category);
    
    /**
     * Найти симптомы для стадии и серьезности.
     */
    @Query("SELECT s FROM CyberpsychosisSymptomEntity s WHERE s.stage = :stage AND s.severity = :severity")
    List<CyberpsychosisSymptomEntity> findByStageAndSeverity(
        CyberpsychosisSymptomEntity.Stage stage,
        CyberpsychosisSymptomEntity.Severity severity
    );
}

