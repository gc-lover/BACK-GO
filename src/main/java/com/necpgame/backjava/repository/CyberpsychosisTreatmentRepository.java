package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.CyberpsychosisTreatmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository для управления методами лечения киберпсихоза (справочник).
 * 
 * Источник: API-SWAGGER/api/v1/gameplay/combat/cyberpsychosis.yaml
 */
@Repository
public interface CyberpsychosisTreatmentRepository extends JpaRepository<CyberpsychosisTreatmentEntity, String> {
    
    /**
     * Найти лечение по типу.
     */
    List<CyberpsychosisTreatmentEntity> findByType(CyberpsychosisTreatmentEntity.TreatmentType type);
    
    /**
     * Найти доступное лечение для стадии.
     */
    @Query("SELECT t FROM CyberpsychosisTreatmentEntity t WHERE t.requiredStage = :stage OR t.requiredStage IS NULL")
    List<CyberpsychosisTreatmentEntity> findAvailableForStage(CyberpsychosisTreatmentEntity.Stage stage);
}

