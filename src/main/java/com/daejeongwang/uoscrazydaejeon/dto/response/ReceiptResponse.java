package com.daejeongwang.uoscrazydaejeon.dto.response;

import com.daejeongwang.uoscrazydaejeon.entity.Receipt;

import java.time.Instant;

public record ReceiptResponse(
        Long receiptId,
        Long visitedPlaceId,
        Long placeId,
        String placeName,
        Receipt.ReceiptStatus verifyStatus,
        Receipt.OcrStatus ocrStatus,
        Instant requestedAt,
        Instant verifiedAt,
        Boolean rewardDrawAvailable
) {
    public static ReceiptResponse from(Receipt receipt) {
        boolean rewardDrawAvailable = receipt.getVerifyStatus() == Receipt.ReceiptStatus.APPROVED;

        return new ReceiptResponse(
                receipt.getId(),
                receipt.getVisitedPlace().getId(),
                receipt.getVisitedPlace().getPlace().getId(),
                receipt.getVisitedPlace().getPlace().getPlaceName(),
                receipt.getVerifyStatus(),
                receipt.getOcrStatus(),
                receipt.getRequestedAt(),
                receipt.getVerifiedAt(),
                rewardDrawAvailable
        );
    }

}
