package com.necpgame.backjava.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.Objects;

/**
 * GameAction - действие в игре
 */
@Schema(description = "Действие в игре")
public class GameAction {

    @JsonProperty("id")
    private String id;

    @JsonProperty("label")
    private String label;

    @JsonProperty("description")
    private String description;

    @JsonProperty("enabled")
    private Boolean enabled = true;

    @Schema(description = "Уникальный идентификатор действия", required = true)
    @NotNull
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Schema(description = "Название действия для отображения", required = true)
    @NotNull
    @Size(min = 1, max = 100)
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Schema(description = "Описание действия")
    @Size(max = 500)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Schema(description = "Доступно ли действие в данный момент")
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameAction that = (GameAction) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(label, that.label) &&
               Objects.equals(description, that.description) &&
               Objects.equals(enabled, that.enabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, label, description, enabled);
    }

    @Override
    public String toString() {
        return "GameAction{" +
                "id='" + id + '\'' +
                ", label='" + label + '\'' +
                ", description='" + description + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}

