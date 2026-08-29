package com.daejeongwang.uoscrazydaejeon.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SimilarRecommendationRequest(
        @JsonProperty("selected_place")
        RecommendationPlaceRequest selectedPlace,
        @JsonProperty("nearby_places")
        List<RecommendationPlaceRequest> nearbyPlaces
) {
}
