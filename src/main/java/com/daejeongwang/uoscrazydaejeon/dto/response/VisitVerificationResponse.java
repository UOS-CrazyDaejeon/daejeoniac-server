package com.daejeongwang.uoscrazydaejeon.dto.response;

import com.daejeongwang.uoscrazydaejeon.entity.Receipt;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class VisitVerificationResponse {
    private Long visitedPlaceId;
    private Long placeId;
    private Instant visitedAt;
    private Long receiptId;
    private UUID receiptUuid;
    private String objectKey;
    private Instant requestedAt;
    private Instant verifiedAt;
    private Receipt.ReceiptStatus verifyStatus;
    private Receipt.OcrStatus ocrStatus;
    private Receipt.VerificationType verificationType;
}
