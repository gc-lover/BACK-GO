package com.necpgame.backjava.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity для таблицы character_subclasses - подклассы персонажей (справочник)
 * Связана с CharacterClass через ManyToOne
 */
@Data
@Entity
@Table(name = "character_subclasses", indexes = {
    @Index(name = "idx_character_subclasses_code", columnList = "subclass_code", unique = true),
    @Index(name = "idx_character_subclasses_class_code", columnList = "class_code")
})
@NoArgsConstructor
@AllArgsConstructor
public class CharacterSubclassEntity {
    
    @Id
    @Column(name = "subclass_code", length = 50, nullable = false)
    private String subclassCode; // solo_assassin, netrunner_hacker, etc.
    
    @Column(name = "name", nullable = false, length = 100)
    private String name; // Assassin, Hacker
    
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_code", referencedColumnName = "class_code", nullable = false)
    private CharacterClassEntity characterClass;
}

