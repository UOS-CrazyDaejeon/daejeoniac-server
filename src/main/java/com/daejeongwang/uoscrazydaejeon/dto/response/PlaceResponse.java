package com.daejeongwang.uoscrazydaejeon.dto.response;

import com.daejeongwang.uoscrazydaejeon.entity.Place;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaceResponse {

    @Schema(description = "장소 ID", example = "1")
    private Long placeId;

    @Schema(description = "장소 이름", example = "성심당 본점")
    private String placeName;

    @Schema(description = "장소 설명", example = "대전을 대표하는 베이커리입니다.")
    private String placeDescription;

    @Schema(description = "장소 주소", example = "대전 중구 대종로480번길 15")
    private String placeAddress;

    @Schema(description = "위도", example = "36.3275")
    private Double latitude;

    @Schema(description = "경도", example = "127.4272")
    private Double longitude;

    @Schema(description = "구", example = "중구")
    private String gu;

    @Schema(description = "동", example = "은행동")
    private String dong;

    @Schema(description = "카테고리", example = "일반 음식점")
    private String category;

    public static PlaceResponse from(Place place) {

        return new PlaceResponse(
                place.getId(),
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
