package com.necpgame.economyservice.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.necpgame.economyservice.model.PriceTrend;
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
 * GetPriceTrends200Response
 */

@JsonTypeName("getPriceTrends_200_response")

public class GetPriceTrends200Response {

  @Valid
  private List<@Valid PriceTrend> trends = new ArrayList<>();

  public GetPriceTrends200Response trends(List<@Valid PriceTrend> trends) {
    this.trends = trends;
    return this;
  }

  public GetPriceTrends200Response addTrendsItem(PriceTrend trendsItem) {
    if (this.trends == null) {
      this.trends = new ArrayList<>();
    }
    this.trends.add(trendsItem);
    return this;
  }

  /**
   * Get trends
   * @return trends
   */
  @Valid 
  @Schema(name = "trends", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("trends")
  public List<@Valid PriceTrend> getTrends() {
    return trends;
  }

  public void setTrends(List<@Valid PriceTrend> trends) {
    this.trends = trends;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GetPriceTrends200Response getPriceTrends200Response = (GetPriceTrends200Response) o;
    return Objects.equals(this.trends, getPriceTrends200Response.trends);
  }

  @Override
  public int hashCode() {
    return Objects.hash(trends);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GetPriceTrends200Response {\n");
    sb.append("    trends: ").append(toIndentedString(trends)).append("\n");
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

