package com.necpgame.backjava.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * MainGameUIDataInventory
 */

@JsonTypeName("MainGameUIData_inventory")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-11-08T02:15:59.618208300+03:00[Europe/Moscow]", comments = "Generator version: 7.17.0")
public class MainGameUIDataInventory {

  @Valid
  private List<String> slots = new ArrayList<>();

  private @Nullable BigDecimal weight;

  public MainGameUIDataInventory slots(List<String> slots) {
    this.slots = slots;
    return this;
  }

  public MainGameUIDataInventory addSlotsItem(String slotsItem) {
    if (this.slots == null) {
      this.slots = new ArrayList<>();
    }
    this.slots.add(slotsItem);
    return this;
  }

  /**
   * Get slots
   * @return slots
   */
  
  @Schema(name = "slots", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("slots")
  public List<String> getSlots() {
    return slots;
  }

  public void setSlots(List<String> slots) {
    this.slots = slots;
  }

  public MainGameUIDataInventory weight(@Nullable BigDecimal weight) {
    this.weight = weight;
    return this;
  }

  /**
   * Get weight
   * @return weight
   */
  @Valid 
  @Schema(name = "weight", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("weight")
  public @Nullable BigDecimal getWeight() {
    return weight;
  }

  public void setWeight(@Nullable BigDecimal weight) {
    this.weight = weight;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MainGameUIDataInventory mainGameUIDataInventory = (MainGameUIDataInventory) o;
    return Objects.equals(this.slots, mainGameUIDataInventory.slots) &&
        Objects.equals(this.weight, mainGameUIDataInventory.weight);
  }

  @Override
  public int hashCode() {
    return Objects.hash(slots, weight);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MainGameUIDataInventory {\n");
    sb.append("    slots: ").append(toIndentedString(slots)).append("\n");
    sb.append("    weight: ").append(toIndentedString(weight)).append("\n");
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

