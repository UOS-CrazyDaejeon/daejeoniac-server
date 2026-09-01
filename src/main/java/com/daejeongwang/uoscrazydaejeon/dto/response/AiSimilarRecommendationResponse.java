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
public class AiSimilarRecommendationResponse {

    @JsonProperty("generated_at")
    private String generatedAt;

    @JsonProperty("selected_place_id")
    private Long selectedPlaceId;

    @JsonProperty("similar_places")
    private List<AiRecommendationResponse> similarPlaces;

    @Setter
    @JsonProperty("session_id")
    private UUID sessionId;
}
