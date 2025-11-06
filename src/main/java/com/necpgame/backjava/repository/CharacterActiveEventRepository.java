package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.CharacterActiveEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * CharacterActiveEventRepository - СЂРµРїРѕР·РёС‚РѕСЂРёР№ РґР»СЏ СЂР°Р±РѕС‚С‹ СЃ Р°РєС‚РёРІРЅС‹РјРё СЃРѕР±С‹С‚РёСЏРјРё РїРµСЂСЃРѕРЅР°Р¶Р°.
 * 
 * РСЃС‚РѕС‡РЅРёРє: API-SWAGGER/api/v1/events/random-events.yaml
 */
@Repository
public interface CharacterActiveEventRepository extends JpaRepository<CharacterActiveEventEntity, UUID> {

    /**
     * РќР°Р№С‚Рё Р°РєС‚РёРІРЅС‹Рµ СЃРѕР±С‹С‚РёСЏ РїРµСЂСЃРѕРЅР°Р¶Р°.
     */
    @Query("SELECT cae FROM CharacterActiveEventEntity cae WHERE cae.characterId = :characterId AND cae.status = 'ACTIVE' ORDER BY cae.createdAt DESC")
    List<CharacterActiveEventEntity> findActiveByCharacterId(UUID characterId);

    /**
     * РќР°Р№С‚Рё РєРѕРЅРєСЂРµС‚РЅРѕРµ Р°РєС‚РёРІРЅРѕРµ СЃРѕР±С‹С‚РёРµ РїРµСЂСЃРѕРЅР°Р¶Р°.
     */
    @Query("SELECT cae FROM CharacterActiveEventEntity cae WHERE cae.characterId = :characterId AND cae.id = :eventId")
    Optional<CharacterActiveEventEntity> findByCharacterIdAndEventId(UUID characterId, UUID eventId);

    /**
     * РџСЂРѕРІРµСЂРёС‚СЊ РµСЃС‚СЊ Р»Рё Р°РєС‚РёРІРЅРѕРµ СЃРѕР±С‹С‚РёРµ.
     */
    @Query("SELECT COUNT(cae) > 0 FROM CharacterActiveEventEntity cae WHERE cae.characterId = :characterId AND cae.eventId = :eventId AND cae.status = 'ACTIVE'")
    boolean hasActiveEvent(UUID characterId, String eventId);
}

