package com.daejeongwang.uoscrazydaejeon.dto.request;

import com.daejeongwang.uoscrazydaejeon.entity.Place;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecommendationPlaceRequest {

    @JsonProperty("place_id")
    private Long placeId;

    @JsonProperty("place_name")
    private String placeName;

    @JsonProperty("description")
    private String placeDescription;

    private String tag;

    @JsonProperty("place_address")
    private String placeAddress;

    private Double latitude;

    private Double longitude;

    private String gu;

    private String dong;

    @JsonProperty("category_large")
    private String categoryLarge;

    @JsonProperty("category_medium")
    private String categoryMedium;

    @JsonProperty("category_small")
    private String categorySmall;

    @JsonProperty("congestion_rate")
    private Double congestionRate;

    @JsonProperty("visitor_count")
    private Long visitorCount;

    @JsonProperty("visited_at")
    private LocalDateTime visitedAt;

    public static RecommendationPlaceRequest from(
            Place place,
            Double congestionRate,
            Long visitorCount,
            LocalDateTime visitedAt
    ) {
        return RecommendationPlaceRequest.builder()
                .placeId(place.getId())
                .placeName(defaultString(place.getPlaceName()))
                .placeDescription(defaultString(place.getPlaceDescription()))
                .tag(createTag(place))
                .placeAddress(defaultString(place.getPlaceAddress()))
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .gu(defaultString(place.getGu()))
                .dong(defaultString(place.getDong()))
                .categoryLarge(defaultString(place.getCategoryLarge()))
                .categoryMedium(defaultString(place.getCategoryMedium()))
                .categorySmall(defaultString(place.getCategorySmall()))
                .congestionRate(congestionRate)
                .visitorCount(visitorCount)
                .visitedAt(visitedAt)
                .build();
    }

    private static String createTag(Place place) {
        if (place.getTag() != null && !place.getTag().isBlank()) {
            return place.getTag();
        }

        return Stream.of(
                        place.getCategoryLarge(),
                        place.getCategoryMedium(),
                        place.getCategorySmall(),
                        place.getGu(),
                        place.getDong()
                )
                .filter(value -> value != null && !value.isBlank())
                .reduce((left, right) -> left + "," + right)
                .orElse("");
    }

    private static String defaultString(String value) {
        return value == null ? "" : value;
    }
}
