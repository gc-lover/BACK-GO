package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.NPCEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * NPCRepository - репозиторий для работы с NPC.
 */
@Repository
public interface NPCRepository extends JpaRepository<NPCEntity, String> {

    /**
     * Найти NPC по ID.
     *
     * @param id ID NPC
     * @return NPC
     */
    Optional<NPCEntity> findById(String id);

    /**
     * Найти всех NPC в локации.
     *
     * @param locationId ID локации
     * @return список NPC
     */
    List<NPCEntity> findByLocationId(String locationId);

    /**
     * Найти всех NPC определенного типа.
     *
     * @param type тип NPC
     * @return список NPC
     */
    List<NPCEntity> findByType(NPCEntity.NPCType type);

    /**
     * Найти всех NPC фракции.
     *
     * @param faction фракция
     * @return список NPC
     */
    List<NPCEntity> findByFaction(String faction);

    /**
     * Найти всех квестодателей в локации.
     *
     * @param locationId ID локации
     * @return список квестодателей
     */
    default List<NPCEntity> findQuestGiversInLocation(String locationId) {
        return findByLocationId(locationId).stream()
                .filter(npc -> npc.getType() == NPCEntity.NPCType.QUEST_GIVER)
                .toList();
    }
}

