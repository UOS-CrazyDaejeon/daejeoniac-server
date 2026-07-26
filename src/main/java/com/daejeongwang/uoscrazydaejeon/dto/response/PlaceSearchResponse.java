package com.daejeongwang.uoscrazydaejeon.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceSearchResponse {

    @Schema(description = "장소 이름", example = "성심당 본점")
    private String name;

    @Schema(description = "지번 주소", example = "대전 중구 은행동 145")
    private String address;

    @Schema(description = "도로명 주소", example = "대전 중구 대종로480번길 15")
    private String roadAddress;

    @Schema(description = "전화번호", example = "1588-8069")
    private String phoneNumber;

    @Schema(description = "위도", example = "36.3275")
    private Double latitude;

    @Schema(description = "경도", example = "127.4272")
    private Double longitude;

    @Schema(description = "카테고리", example = "음식점 > 간식 > 제과,베이커리")
    private String category;

    @Schema(description = "장소 상세 URL", example = "https://place.map.kakao.com/123456789")
    private String url;
}
