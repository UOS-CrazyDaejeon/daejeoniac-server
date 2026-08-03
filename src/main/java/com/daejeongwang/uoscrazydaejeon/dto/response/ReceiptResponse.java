package com.daejeongwang.uoscrazydaejeon.dto.response;

import com.daejeongwang.uoscrazydaejeon.entity.Receipt;

import java.time.LocalDateTime;

public record ReceiptResponse(
        Long receiptId,
        Long visitedPlaceId,
        Long placeId,
        String placeName,
        Receipt.ReceiptStatus verifyStatus,
        Receipt.OcrStatus ocrStatus,
        LocalDateTime createdAt,
        LocalDateTime verifiedAt,
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
                receipt.getCreatedAt(),
                receipt.getVerifiedAt(),
                rewardDrawAvailable
        );
    }

}
