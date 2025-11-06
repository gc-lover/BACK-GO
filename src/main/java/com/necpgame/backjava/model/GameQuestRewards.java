package com.necpgame.backjava.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * GameQuestRewards - награды за квест
 */
@Schema(description = "Награды за квест")
public class GameQuestRewards {

    @JsonProperty("experience")
    private Integer experience;

    @JsonProperty("money")
    private Integer money;

    @JsonProperty("items")
    private List<String> items = new ArrayList<>();

    @JsonProperty("reputation")
    private ReputationChange reputation;

    @Schema(description = "Опыт за выполнение квеста")
    @Min(0)
    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    @Schema(description = "Деньги (eddies) за выполнение квеста")
    @Min(0)
    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    @Schema(description = "Список ID предметов в награду")
    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    @Schema(description = "Изменение репутации")
    @Valid
    public ReputationChange getReputation() {
        return reputation;
    }

    public void setReputation(ReputationChange reputation) {
        this.reputation = reputation;
    }

    /**
     * Изменение репутации с фракцией
     */
    @Schema(description = "Изменение репутации")
    public static class ReputationChange {
        @JsonProperty("faction")
        private String faction;

        @JsonProperty("amount")
        private Integer amount;

        @Schema(description = "Фракция, с которой изменяется репутация")
        public String getFaction() {
            return faction;
        }

        public void setFaction(String faction) {
            this.faction = faction;
        }

        @Schema(description = "Изменение репутации (может быть отрицательным)")
        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ReputationChange that = (ReputationChange) o;
            return Objects.equals(faction, that.faction) &&
                   Objects.equals(amount, that.amount);
        }

        @Override
        public int hashCode() {
            return Objects.hash(faction, amount);
        }

        @Override
        public String toString() {
            return "ReputationChange{" +
                    "faction='" + faction + '\'' +
                    ", amount=" + amount +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameQuestRewards that = (GameQuestRewards) o;
        return Objects.equals(experience, that.experience) &&
               Objects.equals(money, that.money) &&
               Objects.equals(items, that.items) &&
               Objects.equals(reputation, that.reputation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(experience, money, items, reputation);
    }

    @Override
    public String toString() {
        return "GameQuestRewards{" +
                "experience=" + experience +
                ", money=" + money +
                ", items=" + items +
                ", reputation=" + reputation +
                '}';
    }
}

