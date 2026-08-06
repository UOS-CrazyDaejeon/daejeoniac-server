package com.daejeongwang.uoscrazydaejeon.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class VisitorCountResponse {
    @Schema(description = "장소 ID", example = "1")
    private Long placeId;

    @Schema(description = "장소 이름", example = "성심당")
    private String placeName;

    @Schema(description = "날짜별 방문자 수 목록")
    private List<VisitorCountItem> visitorCounts;

    @Getter
    @Builder
    public static class VisitorCountItem {

        @Schema(description = "날짜", example = "2026-07-26")
        private LocalDate date;

        @Schema(description = "방문자 수", example = "1000")
        private Long visitorCount;
    }
}
