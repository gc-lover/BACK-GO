package com.necpgame.backjava.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * TutorialStepsResponse - шаги туториала
 */
@Schema(description = "Шаги туториала")
public class TutorialStepsResponse {

    @JsonProperty("steps")
    private List<TutorialStep> steps = new ArrayList<>();

    @JsonProperty("currentStep")
    private Integer currentStep;

    @JsonProperty("totalSteps")
    private Integer totalSteps;

    @JsonProperty("canSkip")
    private Boolean canSkip;

    @Schema(description = "Список шагов туториала", required = true)
    @NotNull
    @Valid
    public List<TutorialStep> getSteps() {
        return steps;
    }

    public void setSteps(List<TutorialStep> steps) {
        this.steps = steps;
    }

    @Schema(description = "Текущий шаг туториала (0-based индекс)", required = true)
    @NotNull
    @Min(0)
    public Integer getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(Integer currentStep) {
        this.currentStep = currentStep;
    }

    @Schema(description = "Общее количество шагов", required = true)
    @NotNull
    @Min(1)
    public Integer getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(Integer totalSteps) {
        this.totalSteps = totalSteps;
    }

    @Schema(description = "Можно ли пропустить туториал", required = true)
    @NotNull
    public Boolean getCanSkip() {
        return canSkip;
    }

    public void setCanSkip(Boolean canSkip) {
        this.canSkip = canSkip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TutorialStepsResponse that = (TutorialStepsResponse) o;
        return Objects.equals(steps, that.steps) &&
               Objects.equals(currentStep, that.currentStep) &&
               Objects.equals(totalSteps, that.totalSteps) &&
               Objects.equals(canSkip, that.canSkip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(steps, currentStep, totalSteps, canSkip);
    }

    @Override
    public String toString() {
        return "TutorialStepsResponse{" +
                "steps=" + steps +
                ", currentStep=" + currentStep +
                ", totalSteps=" + totalSteps +
                ", canSkip=" + canSkip +
                '}';
    }
}

