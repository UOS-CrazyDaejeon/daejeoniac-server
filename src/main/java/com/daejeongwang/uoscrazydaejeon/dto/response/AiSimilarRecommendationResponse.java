package com.daejeongwang.uoscrazydaejeon.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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
}
