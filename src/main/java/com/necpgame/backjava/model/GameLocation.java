package com.necpgame.backjava.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * GameLocation - локация в игре
 */
@Schema(description = "Локация в игре")
public class GameLocation {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("city")
    private String city;

    @JsonProperty("district")
    private String district;

    @JsonProperty("danger_level")
    private DangerLevelEnum dangerLevel;

    @JsonProperty("min_level")
    private Integer minLevel;

    @JsonProperty("type")
    private TypeEnum type;

    @JsonProperty("connected_locations")
    private List<String> connectedLocations = new ArrayList<>();

    /**
     * Уровень опасности локации
     */
    public enum DangerLevelEnum {
        LOW("low"),
        MEDIUM("medium"),
        HIGH("high");

        private String value;

        DangerLevelEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    /**
     * Тип локации
     */
    public enum TypeEnum {
        CORPORATE("corporate"),
        INDUSTRIAL("industrial"),
        RESIDENTIAL("residential"),
        CRIMINAL("criminal");

        private String value;

        TypeEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    /**
     * Уникальный идентификатор локации
     */
    @Schema(description = "Уникальный идентификатор локации", example = "loc-downtown-001", required = true)
    @NotNull
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Название локации
     */
    @Schema(description = "Название локации", example = "Downtown - Корпоративный центр", required = true)
    @NotNull
    @Size(min = 1, max = 200)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Детальное описание локации
     */
    @Schema(description = "Детальное описание локации", required = true)
    @NotNull
    @Size(min = 10, max = 2000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Город
     */
    @Schema(description = "Город", example = "Night City")
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Район города
     */
    @Schema(description = "Район города", example = "Downtown")
    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    /**
     * Уровень опасности локации
     */
    @Schema(description = "Уровень опасности локации", example = "low", required = true)
    @NotNull
    public DangerLevelEnum getDangerLevel() {
        return dangerLevel;
    }

    public void setDangerLevel(DangerLevelEnum dangerLevel) {
        this.dangerLevel = dangerLevel;
    }

    /**
     * Минимальный уровень персонажа для посещения
     */
    @Schema(description = "Минимальный уровень персонажа для посещения", example = "1")
    @Min(1)
    @Max(100)
    public Integer getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(Integer minLevel) {
        this.minLevel = minLevel;
    }

    /**
     * Тип локации
     */
    @Schema(description = "Тип локации", example = "corporate")
    public TypeEnum getType() {
        return type;
    }

    public void setType(TypeEnum type) {
        this.type = type;
    }

    /**
     * Список ID связанных локаций (для перемещения)
     */
    @Schema(description = "Список ID связанных локаций (для перемещения)")
    public List<String> getConnectedLocations() {
        return connectedLocations;
    }

    public void setConnectedLocations(List<String> connectedLocations) {
        this.connectedLocations = connectedLocations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameLocation that = (GameLocation) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(name, that.name) &&
               Objects.equals(description, that.description) &&
               Objects.equals(city, that.city) &&
               Objects.equals(district, that.district) &&
               Objects.equals(dangerLevel, that.dangerLevel) &&
               Objects.equals(minLevel, that.minLevel) &&
               Objects.equals(type, that.type) &&
               Objects.equals(connectedLocations, that.connectedLocations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, city, district, dangerLevel, minLevel, type, connectedLocations);
    }

    @Override
    public String toString() {
        return "GameLocation{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", dangerLevel=" + dangerLevel +
                ", minLevel=" + minLevel +
                ", type=" + type +
                ", connectedLocations=" + connectedLocations +
                '}';
    }
}

