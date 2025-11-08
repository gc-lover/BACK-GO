package com.necpgame.adminservice.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
 * ChatMessage
 */


public class ChatMessage {

  private @Nullable String messageId;

  private @Nullable String senderId;

  private @Nullable String senderName;

  private @Nullable String channel;

  private @Nullable String message;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime sentAt;

  private @Nullable Object formatting;

  @Valid
  private List<String> mentions = new ArrayList<>();

  public ChatMessage messageId(@Nullable String messageId) {
    this.messageId = messageId;
    return this;
  }

  /**
   * Get messageId
   * @return messageId
   */
  
  @Schema(name = "message_id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("message_id")
  public @Nullable String getMessageId() {
    return messageId;
  }

  public void setMessageId(@Nullable String messageId) {
    this.messageId = messageId;
  }

  public ChatMessage senderId(@Nullable String senderId) {
    this.senderId = senderId;
    return this;
  }

  /**
   * Get senderId
   * @return senderId
   */
  
  @Schema(name = "sender_id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sender_id")
  public @Nullable String getSenderId() {
    return senderId;
  }

  public void setSenderId(@Nullable String senderId) {
    this.senderId = senderId;
  }

  public ChatMessage senderName(@Nullable String senderName) {
    this.senderName = senderName;
    return this;
  }

  /**
   * Get senderName
   * @return senderName
   */
  
  @Schema(name = "sender_name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sender_name")
  public @Nullable String getSenderName() {
    return senderName;
  }

  public void setSenderName(@Nullable String senderName) {
    this.senderName = senderName;
  }

  public ChatMessage channel(@Nullable String channel) {
    this.channel = channel;
    return this;
  }

  /**
   * Get channel
   * @return channel
   */
  
  @Schema(name = "channel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("channel")
  public @Nullable String getChannel() {
    return channel;
  }

  public void setChannel(@Nullable String channel) {
    this.channel = channel;
  }

  public ChatMessage message(@Nullable String message) {
    this.message = message;
    return this;
  }

  /**
   * Get message
   * @return message
   */
  
  @Schema(name = "message", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("message")
  public @Nullable String getMessage() {
    return message;
  }

  public void setMessage(@Nullable String message) {
    this.message = message;
  }

  public ChatMessage sentAt(@Nullable OffsetDateTime sentAt) {
    this.sentAt = sentAt;
    return this;
  }

  /**
   * Get sentAt
   * @return sentAt
   */
  @Valid 
  @Schema(name = "sent_at", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sent_at")
  public @Nullable OffsetDateTime getSentAt() {
    return sentAt;
  }

  public void setSentAt(@Nullable OffsetDateTime sentAt) {
    this.sentAt = sentAt;
  }

  public ChatMessage formatting(@Nullable Object formatting) {
    this.formatting = formatting;
    return this;
  }

  /**
   * Get formatting
   * @return formatting
   */
  
  @Schema(name = "formatting", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("formatting")
  public @Nullable Object getFormatting() {
    return formatting;
  }

  public void setFormatting(@Nullable Object formatting) {
    this.formatting = formatting;
  }

  public ChatMessage mentions(List<String> mentions) {
    this.mentions = mentions;
    return this;
  }

  public ChatMessage addMentionsItem(String mentionsItem) {
    if (this.mentions == null) {
      this.mentions = new ArrayList<>();
    }
    this.mentions.add(mentionsItem);
    return this;
  }

  /**
   * Get mentions
   * @return mentions
   */
  
  @Schema(name = "mentions", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mentions")
  public List<String> getMentions() {
    return mentions;
  }

  public void setMentions(List<String> mentions) {
    this.mentions = mentions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChatMessage chatMessage = (ChatMessage) o;
    return Objects.equals(this.messageId, chatMessage.messageId) &&
        Objects.equals(this.senderId, chatMessage.senderId) &&
        Objects.equals(this.senderName, chatMessage.senderName) &&
        Objects.equals(this.channel, chatMessage.channel) &&
        Objects.equals(this.message, chatMessage.message) &&
        Objects.equals(this.sentAt, chatMessage.sentAt) &&
        Objects.equals(this.formatting, chatMessage.formatting) &&
        Objects.equals(this.mentions, chatMessage.mentions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(messageId, senderId, senderName, channel, message, sentAt, formatting, mentions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChatMessage {\n");
    sb.append("    messageId: ").append(toIndentedString(messageId)).append("\n");
    sb.append("    senderId: ").append(toIndentedString(senderId)).append("\n");
    sb.append("    senderName: ").append(toIndentedString(senderName)).append("\n");
    sb.append("    channel: ").append(toIndentedString(channel)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    sentAt: ").append(toIndentedString(sentAt)).append("\n");
    sb.append("    formatting: ").append(toIndentedString(formatting)).append("\n");
    sb.append("    mentions: ").append(toIndentedString(mentions)).append("\n");
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

