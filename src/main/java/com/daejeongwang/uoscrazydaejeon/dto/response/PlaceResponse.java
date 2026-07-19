package com.daejeongwang.uoscrazydaejeon.dto.response;

import com.daejeongwang.uoscrazydaejeon.entity.Place;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaceResponse {

    private Long placeId;

    private String placeName;

    private String placeDescription;

    private String placeAddress;

    private Double latitude;

    private Double longitude;

    private String gu;

    private String dong;

    private String category;

    public static PlaceResponse from(Place place) {

        return new PlaceResponse(
                place.getPlaceId(),
                place.getPlaceName(),
                place.getPlaceDescription(),
                place.getPlaceAddress(),
                place.getLatitude(),
                place.getLongitude(),
                place.getGu(),
                place.getDong(),
                place.getCategoryLarge()
        );
    }
}
