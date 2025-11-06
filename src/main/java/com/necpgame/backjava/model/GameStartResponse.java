package com.necpgame.backjava.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * GameStartResponse - ответ при начале игры
 */
@Schema(description = "Ответ при начале игры")
public class GameStartResponse {

    @JsonProperty("gameSessionId")
    private UUID gameSessionId;

    @JsonProperty("characterId")
    private UUID characterId;

    @JsonProperty("currentLocation")
    private GameLocation currentLocation;

    @JsonProperty("characterState")
    private GameCharacterState characterState;

    @JsonProperty("startingEquipment")
    private List<GameStartingItem> startingEquipment = new ArrayList<>();

    @JsonProperty("welcomeMessage")
    private String welcomeMessage;

    @JsonProperty("tutorialEnabled")
    private Boolean tutorialEnabled;

    @Schema(description = "ID игровой сессии", required = true)
    @NotNull
    public UUID getGameSessionId() {
        return gameSessionId;
    }

    public void setGameSessionId(UUID gameSessionId) {
        this.gameSessionId = gameSessionId;
    }

    @Schema(description = "ID персонажа", required = true)
    @NotNull
    public UUID getCharacterId() {
        return characterId;
    }

    public void setCharacterId(UUID characterId) {
        this.characterId = characterId;
    }

    @Schema(description = "Текущая локация", required = true)
    @NotNull
    @Valid
    public GameLocation getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(GameLocation currentLocation) {
        this.currentLocation = currentLocation;
    }

    @Schema(description = "Состояние персонажа", required = true)
    @NotNull
    @Valid
    public GameCharacterState getCharacterState() {
        return characterState;
    }

    public void setCharacterState(GameCharacterState characterState) {
        this.characterState = characterState;
    }

    @Schema(description = "Стартовое снаряжение", required = true)
    @NotNull
    @Valid
    public List<GameStartingItem> getStartingEquipment() {
        return startingEquipment;
    }

    public void setStartingEquipment(List<GameStartingItem> startingEquipment) {
        this.startingEquipment = startingEquipment;
    }

    @Schema(description = "Приветственное сообщение", required = true)
    @NotNull
    @Size(min = 10, max = 1000)
    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    @Schema(description = "Включен ли туториал", required = true)
    @NotNull
    public Boolean getTutorialEnabled() {
        return tutorialEnabled;
    }

    public void setTutorialEnabled(Boolean tutorialEnabled) {
        this.tutorialEnabled = tutorialEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameStartResponse that = (GameStartResponse) o;
        return Objects.equals(gameSessionId, that.gameSessionId) &&
               Objects.equals(characterId, that.characterId) &&
               Objects.equals(currentLocation, that.currentLocation) &&
               Objects.equals(characterState, that.characterState) &&
               Objects.equals(startingEquipment, that.startingEquipment) &&
               Objects.equals(welcomeMessage, that.welcomeMessage) &&
               Objects.equals(tutorialEnabled, that.tutorialEnabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameSessionId, characterId, currentLocation, characterState, 
                           startingEquipment, welcomeMessage, tutorialEnabled);
    }

    @Override
    public String toString() {
        return "GameStartResponse{" +
                "gameSessionId=" + gameSessionId +
                ", characterId=" + characterId +
                ", currentLocation=" + currentLocation +
                ", characterState=" + characterState +
                ", startingEquipment=" + startingEquipment +
                ", welcomeMessage='" + welcomeMessage + '\'' +
                ", tutorialEnabled=" + tutorialEnabled +
                '}';
    }
}

