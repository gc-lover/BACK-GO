package com.necpgame.backjava.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entity для таблицы characters - персонажи игроков
 * Соответствует Character DTO из OpenAPI спецификации
 */
@Data
@Entity
@Table(name = "characters", indexes = {
    @Index(name = "idx_characters_account_id", columnList = "account_id"),
    @Index(name = "idx_characters_name", columnList = "name"),
    @Index(name = "idx_characters_class", columnList = "class_code"),
    @Index(name = "idx_characters_origin", columnList = "origin_code"),
    @Index(name = "idx_characters_faction_id", columnList = "faction_id"),
    @Index(name = "idx_characters_city_id", columnList = "city_id")
})
@NoArgsConstructor
@AllArgsConstructor
public class CharacterEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;
    
    @Column(name = "name", nullable = false, length = 20)
    private String name;
    
    @Column(name = "class_code", nullable = false, length = 50)
    private String classCode; // Solo, Netrunner, etc. - ссылка на CharacterClass
    
    @Column(name = "subclass_code", length = 50)
    private String subclassCode; // solo_assassin, etc. - ссылка на CharacterSubclass
    
    @Column(name = "gender", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    @Column(name = "origin_code", nullable = false, length = 50)
    private String originCode; // street_kid, corpo, nomad - ссылка на CharacterOrigin
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faction_id")
    private FactionEntity faction;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private CityEntity city;
    
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "appearance_id", nullable = false)
    private CharacterAppearanceEntity appearance;
    
    @Column(name = "level", nullable = false)
    private Integer level = 1;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    
    @Column(name = "last_login")
    private OffsetDateTime lastLogin;
    
    // Enum для пола
    public enum Gender {
        male,
        female,
        other
    }
}

