package com.necpgame.backjava.mapper;

import com.necpgame.backjava.entity.CharacterClassEntity;
import com.necpgame.backjava.entity.CharacterSubclassEntity;
import com.necpgame.backjava.model.CharacterClass;
import com.necpgame.backjava.model.CharacterClassSubclassesInner;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper для преобразования CharacterClassEntity ↔ CharacterClass DTO
 */
@Component
public class CharacterClassMapper {
    
    /**
     * Преобразовать Entity в DTO
     */
    public CharacterClass toDto(CharacterClassEntity entity) {
        if (entity == null) {
            return null;
        }
        
        CharacterClass dto = new CharacterClass();
        dto.setId(entity.getClassCode());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        
        // Subclasses
        if (entity.getSubclasses() != null) {
            dto.setSubclasses(entity.getSubclasses().stream()
                .map(this::subclassToDto)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    /**
     * Преобразовать Subclass Entity в DTO
     */
    private CharacterClassSubclassesInner subclassToDto(CharacterSubclassEntity entity) {
        if (entity == null) {
            return null;
        }
        
        CharacterClassSubclassesInner dto = new CharacterClassSubclassesInner();
        dto.setId(entity.getSubclassCode());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        
        return dto;
    }
}

