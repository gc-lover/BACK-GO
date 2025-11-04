package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.CharacterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository для работы с персонажами игроков
 */
@Repository
public interface CharacterRepository extends JpaRepository<CharacterEntity, UUID> {
    
    /**
     * Найти все персонажи аккаунта
     */
    @Query("SELECT c FROM CharacterEntity c " +
           "LEFT JOIN FETCH c.city " +
           "LEFT JOIN FETCH c.faction " +
           "LEFT JOIN FETCH c.appearance " +
           "WHERE c.account.id = :accountId")
    List<CharacterEntity> findAllByAccountId(@Param("accountId") UUID accountId);
    
    /**
     * Найти персонажа по ID с загрузкой всех связанных данных
     */
    @Query("SELECT c FROM CharacterEntity c " +
           "LEFT JOIN FETCH c.account " +
           "LEFT JOIN FETCH c.city " +
           "LEFT JOIN FETCH c.faction " +
           "LEFT JOIN FETCH c.appearance " +
           "WHERE c.id = :id")
    Optional<CharacterEntity> findByIdWithDetails(@Param("id") UUID id);
    
    /**
     * Найти персонажа по ID и аккаунту (для проверки владения)
     */
    Optional<CharacterEntity> findByIdAndAccountId(UUID id, UUID accountId);
    
    /**
     * Проверить существование персонажа с таким именем у аккаунта
     */
    boolean existsByNameAndAccountId(String name, UUID accountId);
}

