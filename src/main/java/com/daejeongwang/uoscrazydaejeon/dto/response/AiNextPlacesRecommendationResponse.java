package com.daejeongwang.uoscrazydaejeon.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiNextPlacesRecommendationResponse {

    @JsonProperty("generated_at")
    private String generatedAt;

    @JsonProperty("current_place_id")
    private Long currentPlaceId;

    @JsonProperty("visited_place_ids")
    private List<Long> visitedPlaceIds;

    @JsonProperty("next_places")
    private List<AiRecommendationResponse> nextPlaces;
}
