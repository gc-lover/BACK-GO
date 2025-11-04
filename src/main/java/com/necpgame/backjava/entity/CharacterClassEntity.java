package com.necpgame.backjava.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity для таблицы character_classes - классы персонажей (справочник)
 * Соответствует CharacterClass DTO из OpenAPI спецификации
 */
@Data
@Entity
@Table(name = "character_classes", indexes = {
    @Index(name = "idx_character_classes_code", columnList = "class_code", unique = true)
})
@NoArgsConstructor
@AllArgsConstructor
public class CharacterClassEntity {
    
    @Id
    @Column(name = "class_code", length = 50, nullable = false)
    private String classCode; // solo, netrunner, fixer, etc.
    
    @Column(name = "name", nullable = false, length = 100)
    private String name; // Solo, Netrunner, Fixer
    
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    
    // Relationships
    @OneToMany(mappedBy = "characterClass", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CharacterSubclassEntity> subclasses = new ArrayList<>();
}

