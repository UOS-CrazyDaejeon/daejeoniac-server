package com.daejeongwang.uoscrazydaejeon.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tools.jackson.databind.JsonNode;

import java.util.stream.StreamSupport;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiRecommendationResponse {

    private Integer rank;

    @JsonProperty("place_id")
    private Long placeId;

    private String name;

    private String category;

    private String categoryLarge;

    private String categoryMedium;

    private String categorySmall;

    private String description;

    private String tag;

    @JsonProperty("recommendation_reason")
    @JsonAlias("similarity_reason")
    private String recommendationReason;

    @JsonProperty("tags")
    public void setTags(JsonNode tags) {
        if (tags == null || tags.isNull()) {
            this.tag = null;
            return;
        }

        if (tags.isArray()) {
            this.tag = StreamSupport.stream(tags.spliterator(), false)
                    .map(JsonNode::asText)
                    .reduce((left, right) -> left + "," + right)
                    .orElse(null);
            return;
        }

        this.tag = tags.asText();
    }
}
