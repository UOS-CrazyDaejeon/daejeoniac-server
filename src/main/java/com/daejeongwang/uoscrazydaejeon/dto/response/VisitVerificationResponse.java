package com.daejeongwang.uoscrazydaejeon.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class VisitVerificationResponse {
    private Long visitedPlaceId;
    private Long placeId;
    private LocalDateTime visitedAt;
}
