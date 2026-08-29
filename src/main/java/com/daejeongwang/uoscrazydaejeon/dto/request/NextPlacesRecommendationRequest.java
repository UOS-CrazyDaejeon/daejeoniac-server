package com.daejeongwang.uoscrazydaejeon.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record NextPlacesRecommendationRequest(
        @JsonProperty("selected_place")
        RecommendationPlaceRequest selectedPlace,
        @JsonProperty("nearby_places")
        List<RecommendationPlaceRequest> nearbyPlaces,
        @JsonProperty("visited_places")
        List<RecommendationPlaceRequest> visitedPlaces
) {
}
