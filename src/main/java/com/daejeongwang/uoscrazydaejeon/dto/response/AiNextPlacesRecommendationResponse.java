package com.daejeongwang.uoscrazydaejeon.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

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

    @Setter
    @JsonProperty("session_id")
    private UUID sessionId;
}
