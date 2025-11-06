package com.necpgame.backjava.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * WelcomeScreenResponse - приветственный экран
 */
@Schema(description = "Приветственный экран")
public class WelcomeScreenResponse {

    @JsonProperty("message")
    private String message;

    @JsonProperty("subtitle")
    private String subtitle;

    @JsonProperty("character")
    private WelcomeCharacterInfo character;

    @JsonProperty("startingLocation")
    private String startingLocation;

    @JsonProperty("buttons")
    private List<WelcomeButton> buttons = new ArrayList<>();

    @Schema(description = "Приветственное сообщение", required = true)
    @NotNull
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Schema(description = "Подзаголовок", required = true)
    @NotNull
    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    @Schema(description = "Информация о персонаже", required = true)
    @NotNull
    @Valid
    public WelcomeCharacterInfo getCharacter() {
        return character;
    }

    public void setCharacter(WelcomeCharacterInfo character) {
        this.character = character;
    }

    @Schema(description = "Стартовая локация", required = true)
    @NotNull
    public String getStartingLocation() {
        return startingLocation;
    }

    public void setStartingLocation(String startingLocation) {
        this.startingLocation = startingLocation;
    }

    @Schema(description = "Кнопки", required = true)
    @NotNull
    public List<WelcomeButton> getButtons() {
        return buttons;
    }

    public void setButtons(List<WelcomeButton> buttons) {
        this.buttons = buttons;
    }

    /**
     * Информация о персонаже для приветственного экрана
     */
    @Schema(description = "Информация о персонаже")
    public static class WelcomeCharacterInfo {
        @JsonProperty("name")
        private String name;

        @JsonProperty("class")
        private String characterClass;

        @JsonProperty("level")
        private Integer level;

        @Schema(description = "Имя персонажа", required = true)
        @NotNull
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Schema(description = "Класс персонажа", required = true)
        @NotNull
        public String getCharacterClass() {
            return characterClass;
        }

        public void setCharacterClass(String characterClass) {
            this.characterClass = characterClass;
        }

        @Schema(description = "Уровень персонажа", required = true)
        @NotNull
        public Integer getLevel() {
            return level;
        }

        public void setLevel(Integer level) {
            this.level = level;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WelcomeCharacterInfo that = (WelcomeCharacterInfo) o;
            return Objects.equals(name, that.name) &&
                   Objects.equals(characterClass, that.characterClass) &&
                   Objects.equals(level, that.level);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, characterClass, level);
        }

        @Override
        public String toString() {
            return "WelcomeCharacterInfo{" +
                    "name='" + name + '\'' +
                    ", characterClass='" + characterClass + '\'' +
                    ", level=" + level +
                    '}';
        }
    }

    /**
     * Кнопка на приветственном экране
     */
    @Schema(description = "Кнопка")
    public static class WelcomeButton {
        @JsonProperty("id")
        private String id;

        @JsonProperty("label")
        private String label;

        @Schema(description = "ID кнопки")
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Schema(description = "Текст кнопки")
        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WelcomeButton that = (WelcomeButton) o;
            return Objects.equals(id, that.id) &&
                   Objects.equals(label, that.label);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, label);
        }

        @Override
        public String toString() {
            return "WelcomeButton{" +
                    "id='" + id + '\'' +
                    ", label='" + label + '\'' +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WelcomeScreenResponse that = (WelcomeScreenResponse) o;
        return Objects.equals(message, that.message) &&
               Objects.equals(subtitle, that.subtitle) &&
               Objects.equals(character, that.character) &&
               Objects.equals(startingLocation, that.startingLocation) &&
               Objects.equals(buttons, that.buttons);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, subtitle, character, startingLocation, buttons);
    }

    @Override
    public String toString() {
        return "WelcomeScreenResponse{" +
                "message='" + message + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", character=" + character +
                ", startingLocation='" + startingLocation + '\'' +
                ", buttons=" + buttons +
                '}';
    }
}

