package com.daejeongwang.uoscrazydaejeon.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CongestionResponse {

    @Schema(description = "장소 ID", example = "1")
    private Long placeId;

    @Schema(description = "예상 혼잡도 날짜", example = "2026-07-26")
    private String date;

    @Schema(description = "장소 이름", example = "1")
    private Long placeName;

    @Schema(description = "혼잡도 비율", example = "72")
    private Long congestionRate;
}
