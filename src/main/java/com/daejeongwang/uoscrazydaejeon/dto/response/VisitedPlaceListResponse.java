package com.daejeongwang.uoscrazydaejeon.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class VisitedPlaceListResponse {
    private Long visitedPlaceId;
    private Long placeId;
    private String placeName;
    private Instant visitedAt;
    private ReceiptAvailability receiptAvailability;

    public enum ReceiptAvailability {
        AVAILABLE,
        PROCESSING,
        APPROVED,
        PASSED,
        UNAVAILABLE
    }
}
