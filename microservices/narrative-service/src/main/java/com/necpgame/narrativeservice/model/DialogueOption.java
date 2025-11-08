package com.necpgame.narrativeservice.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.necpgame.narrativeservice.model.DialogueOptionRequiredAttribute;
import com.necpgame.narrativeservice.model.SkillCheckRequirement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.lang.Nullable;
import java.util.NoSuchElementException;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * DialogueOption
 */


public class DialogueOption {

  private @Nullable String optionId;

  private @Nullable String text;

  private JsonNullable<DialogueOptionRequiredAttribute> requiredAttribute = JsonNullable.<DialogueOptionRequiredAttribute>undefined();

  private @Nullable SkillCheckRequirement skillCheck;

  private @Nullable String leadsToNode;

  @Valid
  private Map<String, Object> effects = new HashMap<>();

  public DialogueOption optionId(@Nullable String optionId) {
    this.optionId = optionId;
    return this;
  }

  /**
   * Get optionId
   * @return optionId
   */
  
  @Schema(name = "option_id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("option_id")
  public @Nullable String getOptionId() {
    return optionId;
  }

  public void setOptionId(@Nullable String optionId) {
    this.optionId = optionId;
  }

  public DialogueOption text(@Nullable String text) {
    this.text = text;
    return this;
  }

  /**
   * Get text
   * @return text
   */
  
  @Schema(name = "text", example = "[Corpo] I'm always ready for business.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("text")
  public @Nullable String getText() {
    return text;
  }

  public void setText(@Nullable String text) {
    this.text = text;
  }

  public DialogueOption requiredAttribute(DialogueOptionRequiredAttribute requiredAttribute) {
    this.requiredAttribute = JsonNullable.of(requiredAttribute);
    return this;
  }

  /**
   * Get requiredAttribute
   * @return requiredAttribute
   */
  @Valid 
  @Schema(name = "required_attribute", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("required_attribute")
  public JsonNullable<DialogueOptionRequiredAttribute> getRequiredAttribute() {
    return requiredAttribute;
  }

  public void setRequiredAttribute(JsonNullable<DialogueOptionRequiredAttribute> requiredAttribute) {
    this.requiredAttribute = requiredAttribute;
  }

  public DialogueOption skillCheck(@Nullable SkillCheckRequirement skillCheck) {
    this.skillCheck = skillCheck;
    return this;
  }

  /**
   * Get skillCheck
   * @return skillCheck
   */
  @Valid 
  @Schema(name = "skill_check", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("skill_check")
  public @Nullable SkillCheckRequirement getSkillCheck() {
    return skillCheck;
  }

  public void setSkillCheck(@Nullable SkillCheckRequirement skillCheck) {
    this.skillCheck = skillCheck;
  }

  public DialogueOption leadsToNode(@Nullable String leadsToNode) {
    this.leadsToNode = leadsToNode;
    return this;
  }

  /**
   * Следующий node диалога
   * @return leadsToNode
   */
  
  @Schema(name = "leads_to_node", description = "Следующий node диалога", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("leads_to_node")
  public @Nullable String getLeadsToNode() {
    return leadsToNode;
  }

  public void setLeadsToNode(@Nullable String leadsToNode) {
    this.leadsToNode = leadsToNode;
  }

  public DialogueOption effects(Map<String, Object> effects) {
    this.effects = effects;
    return this;
  }

  public DialogueOption putEffectsItem(String key, Object effectsItem) {
    if (this.effects == null) {
      this.effects = new HashMap<>();
    }
    this.effects.put(key, effectsItem);
    return this;
  }

  /**
   * Эффекты выбора (flags, reputation)
   * @return effects
   */
  
  @Schema(name = "effects", description = "Эффекты выбора (flags, reputation)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("effects")
  public Map<String, Object> getEffects() {
    return effects;
  }

  public void setEffects(Map<String, Object> effects) {
    this.effects = effects;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DialogueOption dialogueOption = (DialogueOption) o;
    return Objects.equals(this.optionId, dialogueOption.optionId) &&
        Objects.equals(this.text, dialogueOption.text) &&
        equalsNullable(this.requiredAttribute, dialogueOption.requiredAttribute) &&
        Objects.equals(this.skillCheck, dialogueOption.skillCheck) &&
        Objects.equals(this.leadsToNode, dialogueOption.leadsToNode) &&
        Objects.equals(this.effects, dialogueOption.effects);
  }

  private static <T> boolean equalsNullable(JsonNullable<T> a, JsonNullable<T> b) {
    return a == b || (a != null && b != null && a.isPresent() && b.isPresent() && Objects.deepEquals(a.get(), b.get()));
  }

  @Override
  public int hashCode() {
    return Objects.hash(optionId, text, hashCodeNullable(requiredAttribute), skillCheck, leadsToNode, effects);
  }

  private static <T> int hashCodeNullable(JsonNullable<T> a) {
    if (a == null) {
      return 1;
    }
    return a.isPresent() ? Arrays.deepHashCode(new Object[]{a.get()}) : 31;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DialogueOption {\n");
    sb.append("    optionId: ").append(toIndentedString(optionId)).append("\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
    sb.append("    requiredAttribute: ").append(toIndentedString(requiredAttribute)).append("\n");
    sb.append("    skillCheck: ").append(toIndentedString(skillCheck)).append("\n");
    sb.append("    leadsToNode: ").append(toIndentedString(leadsToNode)).append("\n");
    sb.append("    effects: ").append(toIndentedString(effects)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

