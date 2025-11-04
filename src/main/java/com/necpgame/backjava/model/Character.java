package com.necpgame.backjava.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.necpgame.backjava.model.CharacterAppearance;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.UUID;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.format.annotation.DateTimeFormat;
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
 * Character
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.17.0")
public class Character {

  private UUID id;

  private UUID accountId;

  private String name;

  /**
   * Класс персонажа
   */
  public enum PropertyClassEnum {
    SOLO("Solo"),
    
    NETRUNNER("Netrunner"),
    
    FIXER("Fixer"),
    
    ROCKERBOY("Rockerboy"),
    
    MEDIA("Media"),
    
    NOMAD("Nomad"),
    
    CORPO("Corpo"),
    
    LAWMAN("Lawman"),
    
    MEDTECH("Medtech"),
    
    TECHIE("Techie"),
    
    POLITICIAN("Politician"),
    
    TRADER("Trader"),
    
    TEACHER("Teacher");

    private final String value;

    PropertyClassEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static PropertyClassEnum fromValue(String value) {
      for (PropertyClassEnum b : PropertyClassEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private PropertyClassEnum propertyClass;

  private JsonNullable<String> subclass = JsonNullable.<String>undefined();

  /**
   * Пол персонажа
   */
  public enum GenderEnum {
    MALE("male"),
    
    FEMALE("female"),
    
    OTHER("other");

    private final String value;

    GenderEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static GenderEnum fromValue(String value) {
      for (GenderEnum b : GenderEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private GenderEnum gender;

  /**
   * Происхождение персонажа
   */
  public enum OriginEnum {
    STREET_KID("street_kid"),
    
    CORPO("corpo"),
    
    NOMAD("nomad");

    private final String value;

    OriginEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static OriginEnum fromValue(String value) {
      for (OriginEnum b : OriginEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private OriginEnum origin;

  private JsonNullable<UUID> factionId = JsonNullable.<UUID>undefined();

  private JsonNullable<String> factionName = JsonNullable.<String>undefined();

  private UUID cityId;

  private String cityName;

  private CharacterAppearance appearance;

  private Integer level = 1;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private JsonNullable<OffsetDateTime> lastLogin = JsonNullable.<OffsetDateTime>undefined();

  public Character() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Character(UUID id, UUID accountId, String name, PropertyClassEnum propertyClass, GenderEnum gender, OriginEnum origin, UUID cityId, String cityName, CharacterAppearance appearance, Integer level, OffsetDateTime createdAt) {
    this.id = id;
    this.accountId = accountId;
    this.name = name;
    this.propertyClass = propertyClass;
    this.gender = gender;
    this.origin = origin;
    this.cityId = cityId;
    this.cityName = cityName;
    this.appearance = appearance;
    this.level = level;
    this.createdAt = createdAt;
  }

  public Character id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * Уникальный идентификатор персонажа
   * @return id
   */
  @NotNull @Valid 
  @Schema(name = "id", example = "550e8400-e29b-41d4-a716-446655440000", description = "Уникальный идентификатор персонажа", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Character accountId(UUID accountId) {
    this.accountId = accountId;
    return this;
  }

  /**
   * Идентификатор аккаунта владельца
   * @return accountId
   */
  @NotNull @Valid 
  @Schema(name = "account_id", example = "550e8400-e29b-41d4-a716-446655440001", description = "Идентификатор аккаунта владельца", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("account_id")
  public UUID getAccountId() {
    return accountId;
  }

  public void setAccountId(UUID accountId) {
    this.accountId = accountId;
  }

  public Character name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Имя персонажа
   * @return name
   */
  @NotNull 
  @Schema(name = "name", example = "John Doe", description = "Имя персонажа", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Character propertyClass(PropertyClassEnum propertyClass) {
    this.propertyClass = propertyClass;
    return this;
  }

  /**
   * Класс персонажа
   * @return propertyClass
   */
  @NotNull 
  @Schema(name = "class", example = "Solo", description = "Класс персонажа", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("class")
  public PropertyClassEnum getPropertyClass() {
    return propertyClass;
  }

  public void setPropertyClass(PropertyClassEnum propertyClass) {
    this.propertyClass = propertyClass;
  }

  public Character subclass(String subclass) {
    this.subclass = JsonNullable.of(subclass);
    return this;
  }

  /**
   * Подкласс персонажа
   * @return subclass
   */
  
  @Schema(name = "subclass", description = "Подкласс персонажа", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("subclass")
  public JsonNullable<String> getSubclass() {
    return subclass;
  }

  public void setSubclass(JsonNullable<String> subclass) {
    this.subclass = subclass;
  }

  public Character gender(GenderEnum gender) {
    this.gender = gender;
    return this;
  }

  /**
   * Пол персонажа
   * @return gender
   */
  @NotNull 
  @Schema(name = "gender", example = "male", description = "Пол персонажа", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("gender")
  public GenderEnum getGender() {
    return gender;
  }

  public void setGender(GenderEnum gender) {
    this.gender = gender;
  }

  public Character origin(OriginEnum origin) {
    this.origin = origin;
    return this;
  }

  /**
   * Происхождение персонажа
   * @return origin
   */
  @NotNull 
  @Schema(name = "origin", example = "street_kid", description = "Происхождение персонажа", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("origin")
  public OriginEnum getOrigin() {
    return origin;
  }

  public void setOrigin(OriginEnum origin) {
    this.origin = origin;
  }

  public Character factionId(UUID factionId) {
    this.factionId = JsonNullable.of(factionId);
    return this;
  }

  /**
   * Идентификатор фракции
   * @return factionId
   */
  @Valid 
  @Schema(name = "faction_id", description = "Идентификатор фракции", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("faction_id")
  public JsonNullable<UUID> getFactionId() {
    return factionId;
  }

  public void setFactionId(JsonNullable<UUID> factionId) {
    this.factionId = factionId;
  }

  public Character factionName(String factionName) {
    this.factionName = JsonNullable.of(factionName);
    return this;
  }

  /**
   * Название фракции
   * @return factionName
   */
  
  @Schema(name = "faction_name", description = "Название фракции", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("faction_name")
  public JsonNullable<String> getFactionName() {
    return factionName;
  }

  public void setFactionName(JsonNullable<String> factionName) {
    this.factionName = factionName;
  }

  public Character cityId(UUID cityId) {
    this.cityId = cityId;
    return this;
  }

  /**
   * Идентификатор стартового города
   * @return cityId
   */
  @NotNull @Valid 
  @Schema(name = "city_id", example = "550e8400-e29b-41d4-a716-446655440000", description = "Идентификатор стартового города", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("city_id")
  public UUID getCityId() {
    return cityId;
  }

  public void setCityId(UUID cityId) {
    this.cityId = cityId;
  }

  public Character cityName(String cityName) {
    this.cityName = cityName;
    return this;
  }

  /**
   * Название стартового города
   * @return cityName
   */
  @NotNull 
  @Schema(name = "city_name", example = "Night City", description = "Название стартового города", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("city_name")
  public String getCityName() {
    return cityName;
  }

  public void setCityName(String cityName) {
    this.cityName = cityName;
  }

  public Character appearance(CharacterAppearance appearance) {
    this.appearance = appearance;
    return this;
  }

  /**
   * Get appearance
   * @return appearance
   */
  @NotNull @Valid 
  @Schema(name = "appearance", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("appearance")
  public CharacterAppearance getAppearance() {
    return appearance;
  }

  public void setAppearance(CharacterAppearance appearance) {
    this.appearance = appearance;
  }

  public Character level(Integer level) {
    this.level = level;
    return this;
  }

  /**
   * Уровень персонажа
   * minimum: 1
   * @return level
   */
  @NotNull @Min(value = 1) 
  @Schema(name = "level", example = "1", description = "Уровень персонажа", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("level")
  public Integer getLevel() {
    return level;
  }

  public void setLevel(Integer level) {
    this.level = level;
  }

  public Character createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Дата создания персонажа
   * @return createdAt
   */
  @NotNull @Valid 
  @Schema(name = "created_at", example = "2025-01-27T12:00Z", description = "Дата создания персонажа", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("created_at")
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public Character lastLogin(OffsetDateTime lastLogin) {
    this.lastLogin = JsonNullable.of(lastLogin);
    return this;
  }

  /**
   * Дата последнего входа в игру
   * @return lastLogin
   */
  @Valid 
  @Schema(name = "last_login", example = "2025-01-27T12:00Z", description = "Дата последнего входа в игру", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("last_login")
  public JsonNullable<OffsetDateTime> getLastLogin() {
    return lastLogin;
  }

  public void setLastLogin(JsonNullable<OffsetDateTime> lastLogin) {
    this.lastLogin = lastLogin;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Character character = (Character) o;
    return Objects.equals(this.id, character.id) &&
        Objects.equals(this.accountId, character.accountId) &&
        Objects.equals(this.name, character.name) &&
        Objects.equals(this.propertyClass, character.propertyClass) &&
        equalsNullable(this.subclass, character.subclass) &&
        Objects.equals(this.gender, character.gender) &&
        Objects.equals(this.origin, character.origin) &&
        equalsNullable(this.factionId, character.factionId) &&
        equalsNullable(this.factionName, character.factionName) &&
        Objects.equals(this.cityId, character.cityId) &&
        Objects.equals(this.cityName, character.cityName) &&
        Objects.equals(this.appearance, character.appearance) &&
        Objects.equals(this.level, character.level) &&
        Objects.equals(this.createdAt, character.createdAt) &&
        equalsNullable(this.lastLogin, character.lastLogin);
  }

  private static <T> boolean equalsNullable(JsonNullable<T> a, JsonNullable<T> b) {
    return a == b || (a != null && b != null && a.isPresent() && b.isPresent() && Objects.deepEquals(a.get(), b.get()));
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, accountId, name, propertyClass, hashCodeNullable(subclass), gender, origin, hashCodeNullable(factionId), hashCodeNullable(factionName), cityId, cityName, appearance, level, createdAt, hashCodeNullable(lastLogin));
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
    sb.append("class Character {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    accountId: ").append(toIndentedString(accountId)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    propertyClass: ").append(toIndentedString(propertyClass)).append("\n");
    sb.append("    subclass: ").append(toIndentedString(subclass)).append("\n");
    sb.append("    gender: ").append(toIndentedString(gender)).append("\n");
    sb.append("    origin: ").append(toIndentedString(origin)).append("\n");
    sb.append("    factionId: ").append(toIndentedString(factionId)).append("\n");
    sb.append("    factionName: ").append(toIndentedString(factionName)).append("\n");
    sb.append("    cityId: ").append(toIndentedString(cityId)).append("\n");
    sb.append("    cityName: ").append(toIndentedString(cityName)).append("\n");
    sb.append("    appearance: ").append(toIndentedString(appearance)).append("\n");
    sb.append("    level: ").append(toIndentedString(level)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    lastLogin: ").append(toIndentedString(lastLogin)).append("\n");
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

