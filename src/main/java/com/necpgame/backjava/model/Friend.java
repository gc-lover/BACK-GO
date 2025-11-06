package com.necpgame.backjava.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Friend
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.17.0")
public class Friend {

  private @Nullable String playerId;

  private @Nullable String playerName;

  private @Nullable Boolean isOnline;

  private @Nullable Object currentCharacter;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime addedAt;

  public Friend playerId(@Nullable String playerId) {
    this.playerId = playerId;
    return this;
  }

  /**
   * Get playerId
   * @return playerId
   */
  
  @Schema(name = "player_id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("player_id")
  public @Nullable String getPlayerId() {
    return playerId;
  }

  public void setPlayerId(@Nullable String playerId) {
    this.playerId = playerId;
  }

  public Friend playerName(@Nullable String playerName) {
    this.playerName = playerName;
    return this;
  }

  /**
   * Get playerName
   * @return playerName
   */
  
  @Schema(name = "player_name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("player_name")
  public @Nullable String getPlayerName() {
    return playerName;
  }

  public void setPlayerName(@Nullable String playerName) {
    this.playerName = playerName;
  }

  public Friend isOnline(@Nullable Boolean isOnline) {
    this.isOnline = isOnline;
    return this;
  }

  /**
   * Get isOnline
   * @return isOnline
   */
  
  @Schema(name = "is_online", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("is_online")
  public @Nullable Boolean getIsOnline() {
    return isOnline;
  }

  public void setIsOnline(@Nullable Boolean isOnline) {
    this.isOnline = isOnline;
  }

  public Friend currentCharacter(@Nullable Object currentCharacter) {
    this.currentCharacter = currentCharacter;
    return this;
  }

  /**
   * Get currentCharacter
   * @return currentCharacter
   */
  
  @Schema(name = "current_character", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("current_character")
  public @Nullable Object getCurrentCharacter() {
    return currentCharacter;
  }

  public void setCurrentCharacter(@Nullable Object currentCharacter) {
    this.currentCharacter = currentCharacter;
  }

  public Friend addedAt(@Nullable OffsetDateTime addedAt) {
    this.addedAt = addedAt;
    return this;
  }

  /**
   * Get addedAt
   * @return addedAt
   */
  @Valid 
  @Schema(name = "added_at", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("added_at")
  public @Nullable OffsetDateTime getAddedAt() {
    return addedAt;
  }

  public void setAddedAt(@Nullable OffsetDateTime addedAt) {
    this.addedAt = addedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Friend friend = (Friend) o;
    return Objects.equals(this.playerId, friend.playerId) &&
        Objects.equals(this.playerName, friend.playerName) &&
        Objects.equals(this.isOnline, friend.isOnline) &&
        Objects.equals(this.currentCharacter, friend.currentCharacter) &&
        Objects.equals(this.addedAt, friend.addedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(playerId, playerName, isOnline, currentCharacter, addedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Friend {\n");
    sb.append("    playerId: ").append(toIndentedString(playerId)).append("\n");
    sb.append("    playerName: ").append(toIndentedString(playerName)).append("\n");
    sb.append("    isOnline: ").append(toIndentedString(isOnline)).append("\n");
    sb.append("    currentCharacter: ").append(toIndentedString(currentCharacter)).append("\n");
    sb.append("    addedAt: ").append(toIndentedString(addedAt)).append("\n");
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

