package com.daejeongwang.uoscrazydaejeon.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class RegionalVisitorCountResponse {
    @Schema(description = "조회 날짜", example = "2026-07-12")
    private LocalDate date;

    @Schema(description = "지역 방문자 수 목록")
    private List<RegionalVisitorCountItem> regionalVisitorCounts;

    @Getter
    @Builder
    public static class RegionalVisitorCountItem {
        @Schema(description = "시군구 코드", example = "30110")
        private String signguCode;

        @Schema(description = "시군구 이름", example = "동구")
        private String signguName;

        @Schema(description = "현지인 방문자 수", example = "97534.5")
        private BigDecimal localVisitorCount;

        @Schema(description = "외지인 방문자 수", example = "117719.0")
        private BigDecimal outsiderVisitorCount;

        @Schema(description = "외국인 방문자 수", example = "1392.54")
        private BigDecimal foreignVisitorCount;
    }
}
