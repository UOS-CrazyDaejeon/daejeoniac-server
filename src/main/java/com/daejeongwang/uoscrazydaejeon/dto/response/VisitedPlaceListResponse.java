package com.daejeongwang.uoscrazydaejeon.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class VisitedPlaceListResponse {
    private Long visitedPlaceId;
    private Long placeId;
    private String placeName;
    private LocalDateTime visitedAt;
    private ReceiptAvailability receiptAvailability;

    public enum ReceiptAvailability {
        AVAILABLE,
        PROCESSING,
        APPROVED,
        UNAVAILABLE
    }
}
