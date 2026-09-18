package com.daejeongwang.uoscrazydaejeon.dto.response;

import com.daejeongwang.uoscrazydaejeon.entity.Place;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaceDetailResponse {

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

    @Schema(description = "카테고리", example = "여행")
    private String categoryLarge;

    @Schema(description = "카테고리", example = "관광, 명소")
    private String categoryMedium;

    @Schema(description = "카테고리", example = "테마파크")
    private String categorySmall;

    @Schema(description = "장소 조회 수", example = "12")
    private Long viewerCount;

    @Schema(description = "로그인한 사용자의 한국 날짜 기준 오늘 방문 인증 여부", example = "true")
    private boolean visitedToday;

    @Schema(description = "다음 장소 추천을 통해 조회한 경우의 추천 세션 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private UUID sessionId;

    public static PlaceDetailResponse from(Place place, Long viewerCount, boolean visitedToday, UUID sessionId) {

        return new PlaceDetailResponse(
                place.getId(),
                place.getPlaceName(),
                place.getPlaceDescription(),
                place.getPlaceAddress(),
                place.getLatitude(),
                place.getLongitude(),
                place.getGu(),
                place.getDong(),
                place.getCategoryLarge(),
                place.getCategoryMedium(),
                place.getCategorySmall(),
                viewerCount,
                visitedToday,
                sessionId
        );
    }
}
