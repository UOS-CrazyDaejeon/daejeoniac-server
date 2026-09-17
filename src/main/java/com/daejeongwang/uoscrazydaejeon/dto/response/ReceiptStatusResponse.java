package com.daejeongwang.uoscrazydaejeon.dto.response;

import com.daejeongwang.uoscrazydaejeon.entity.Receipt;
import lombok.Builder;
import lombok.Getter;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReceiptStatusResponse {
    private Long receiptId;
    private Long placeId;
    private String placeName;
    private Receipt.ReceiptStatus verifyStatus;
    private Receipt.OcrStatus ocrStatus;
    private Receipt.VerificationType verificationType;
    boolean gachaAvailable;

    // TODO: OCR 원문 정보 임시 노출. 프론트 계약 확정 후 전용 응답 DTO로 정리한다.
    private String ocrPlaceAddress;
    private Instant ocrPaidAt;
}
