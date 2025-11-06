package com.necpgame.backjava.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.Objects;

/**
 * GameCharacterState - состояние персонажа в игре
 */
@Schema(description = "Состояние персонажа в игре")
public class GameCharacterState {

    @JsonProperty("health")
    private Integer health;

    @JsonProperty("energy")
    private Integer energy;

    @JsonProperty("humanity")
    private Integer humanity;

    @JsonProperty("money")
    private Integer money;

    @JsonProperty("level")
    private Integer level;

    @JsonProperty("experience")
    private Integer experience;

    @Schema(description = "Здоровье персонажа", example = "100", required = true)
    @NotNull
    @Min(0)
    @Max(100)
    public Integer getHealth() {
        return health;
    }

    public void setHealth(Integer health) {
        this.health = health;
    }

    @Schema(description = "Энергия персонажа", example = "100", required = true)
    @NotNull
    @Min(0)
    @Max(100)
    public Integer getEnergy() {
        return energy;
    }

    public void setEnergy(Integer energy) {
        this.energy = energy;
    }

    @Schema(description = "Человечность персонажа", example = "100", required = true)
    @NotNull
    @Min(0)
    @Max(100)
    public Integer getHumanity() {
        return humanity;
    }

    public void setHumanity(Integer humanity) {
        this.humanity = humanity;
    }

    @Schema(description = "Деньги (eddies)", example = "500", required = true)
    @NotNull
    @Min(0)
    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    @Schema(description = "Уровень персонажа", example = "1", required = true)
    @NotNull
    @Min(1)
    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @Schema(description = "Опыт персонажа", example = "0")
    @Min(0)
    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameCharacterState that = (GameCharacterState) o;
        return Objects.equals(health, that.health) &&
               Objects.equals(energy, that.energy) &&
               Objects.equals(humanity, that.humanity) &&
               Objects.equals(money, that.money) &&
               Objects.equals(level, that.level) &&
               Objects.equals(experience, that.experience);
    }

    @Override
    public int hashCode() {
        return Objects.hash(health, energy, humanity, money, level, experience);
    }

    @Override
    public String toString() {
        return "GameCharacterState{" +
                "health=" + health +
                ", energy=" + energy +
                ", humanity=" + humanity +
                ", money=" + money +
                ", level=" + level +
                ", experience=" + experience +
                '}';
    }
}

