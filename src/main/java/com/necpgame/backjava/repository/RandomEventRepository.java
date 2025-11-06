package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.RandomEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RandomEventRepository - СЂРµРїРѕР·РёС‚РѕСЂРёР№ РґР»СЏ СЂР°Р±РѕС‚С‹ СЃРѕ СЃР»СѓС‡Р°Р№РЅС‹РјРё СЃРѕР±С‹С‚РёСЏРјРё.
 * 
 * РСЃС‚РѕС‡РЅРёРє: API-SWAGGER/api/v1/events/random-events.yaml
 */
@Repository
public interface RandomEventRepository extends JpaRepository<RandomEventEntity, String> {

    /**
     * РќР°Р№С‚Рё СЃРѕР±С‹С‚РёСЏ РїРѕ С‚РёРїСѓ.
     */
    @Query("SELECT e FROM RandomEventEntity e WHERE e.eventType = :type AND e.active = true ORDER BY e.rarity, e.title")
    List<RandomEventEntity> findByType(String type);

    /**
     * РќР°Р№С‚Рё Р°РєС‚РёРІРЅС‹Рµ СЃРѕР±С‹С‚РёСЏ РґР»СЏ СѓСЂРѕРІРЅСЏ.
     */
    @Query("SELECT e FROM RandomEventEntity e WHERE e.active = true AND e.minLevel <= :level AND (e.maxLevel IS NULL OR e.maxLevel >= :level) ORDER BY e.rarity")
    List<RandomEventEntity> findByLevel(Integer level);

    /**
     * РќР°Р№С‚Рё РІСЃРµ Р°РєС‚РёРІРЅС‹Рµ СЃРѕР±С‹С‚РёСЏ.
     */
    @Query("SELECT e FROM RandomEventEntity e WHERE e.active = true ORDER BY e.rarity, e.title")
    List<RandomEventEntity> findAllActive();
}

