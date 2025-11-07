package com.necpgame.backjava.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.necpgame.backjava.model.MainGameUIDataCharacter;
import com.necpgame.backjava.model.MainGameUIDataInventory;
import com.necpgame.backjava.model.MainGameUIDataWorldState;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
 * MainGameUIData
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-11-08T02:15:59.618208300+03:00[Europe/Moscow]", comments = "Generator version: 7.17.0")
public class MainGameUIData {

  private @Nullable MainGameUIDataCharacter character;

  @Valid
  private List<Object> activeQuests = new ArrayList<>();

  private @Nullable MainGameUIDataInventory inventory;

  private JsonNullable<Object> party = JsonNullable.<Object>undefined();

  @Valid
  private List<Object> notifications = new ArrayList<>();

  private @Nullable MainGameUIDataWorldState worldState;

  public MainGameUIData character(@Nullable MainGameUIDataCharacter character) {
    this.character = character;
    return this;
  }

  /**
   * Get character
   * @return character
   */
  @Valid 
  @Schema(name = "character", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("character")
  public @Nullable MainGameUIDataCharacter getCharacter() {
    return character;
  }

  public void setCharacter(@Nullable MainGameUIDataCharacter character) {
    this.character = character;
  }

  public MainGameUIData activeQuests(List<Object> activeQuests) {
    this.activeQuests = activeQuests;
    return this;
  }

  public MainGameUIData addActiveQuestsItem(Object activeQuestsItem) {
    if (this.activeQuests == null) {
      this.activeQuests = new ArrayList<>();
    }
    this.activeQuests.add(activeQuestsItem);
    return this;
  }

  /**
   * Get activeQuests
   * @return activeQuests
   */
  
  @Schema(name = "active_quests", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("active_quests")
  public List<Object> getActiveQuests() {
    return activeQuests;
  }

  public void setActiveQuests(List<Object> activeQuests) {
    this.activeQuests = activeQuests;
  }

  public MainGameUIData inventory(@Nullable MainGameUIDataInventory inventory) {
    this.inventory = inventory;
    return this;
  }

  /**
   * Get inventory
   * @return inventory
   */
  @Valid 
  @Schema(name = "inventory", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("inventory")
  public @Nullable MainGameUIDataInventory getInventory() {
    return inventory;
  }

  public void setInventory(@Nullable MainGameUIDataInventory inventory) {
    this.inventory = inventory;
  }

  public MainGameUIData party(Object party) {
    this.party = JsonNullable.of(party);
    return this;
  }

  /**
   * Get party
   * @return party
   */
  
  @Schema(name = "party", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("party")
  public JsonNullable<Object> getParty() {
    return party;
  }

  public void setParty(JsonNullable<Object> party) {
    this.party = party;
  }

  public MainGameUIData notifications(List<Object> notifications) {
    this.notifications = notifications;
    return this;
  }

  public MainGameUIData addNotificationsItem(Object notificationsItem) {
    if (this.notifications == null) {
      this.notifications = new ArrayList<>();
    }
    this.notifications.add(notificationsItem);
    return this;
  }

  /**
   * Get notifications
   * @return notifications
   */
  
  @Schema(name = "notifications", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("notifications")
  public List<Object> getNotifications() {
    return notifications;
  }

  public void setNotifications(List<Object> notifications) {
    this.notifications = notifications;
  }

  public MainGameUIData worldState(@Nullable MainGameUIDataWorldState worldState) {
    this.worldState = worldState;
    return this;
  }

  /**
   * Get worldState
   * @return worldState
   */
  @Valid 
  @Schema(name = "world_state", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("world_state")
  public @Nullable MainGameUIDataWorldState getWorldState() {
    return worldState;
  }

  public void setWorldState(@Nullable MainGameUIDataWorldState worldState) {
    this.worldState = worldState;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MainGameUIData mainGameUIData = (MainGameUIData) o;
    return Objects.equals(this.character, mainGameUIData.character) &&
        Objects.equals(this.activeQuests, mainGameUIData.activeQuests) &&
        Objects.equals(this.inventory, mainGameUIData.inventory) &&
        equalsNullable(this.party, mainGameUIData.party) &&
        Objects.equals(this.notifications, mainGameUIData.notifications) &&
        Objects.equals(this.worldState, mainGameUIData.worldState);
  }

  private static <T> boolean equalsNullable(JsonNullable<T> a, JsonNullable<T> b) {
    return a == b || (a != null && b != null && a.isPresent() && b.isPresent() && Objects.deepEquals(a.get(), b.get()));
  }

  @Override
  public int hashCode() {
    return Objects.hash(character, activeQuests, inventory, hashCodeNullable(party), notifications, worldState);
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
    sb.append("class MainGameUIData {\n");
    sb.append("    character: ").append(toIndentedString(character)).append("\n");
    sb.append("    activeQuests: ").append(toIndentedString(activeQuests)).append("\n");
    sb.append("    inventory: ").append(toIndentedString(inventory)).append("\n");
    sb.append("    party: ").append(toIndentedString(party)).append("\n");
    sb.append("    notifications: ").append(toIndentedString(notifications)).append("\n");
    sb.append("    worldState: ").append(toIndentedString(worldState)).append("\n");
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

