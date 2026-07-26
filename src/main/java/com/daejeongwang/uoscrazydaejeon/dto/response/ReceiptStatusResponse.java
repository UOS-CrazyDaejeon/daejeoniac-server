package com.daejeongwang.uoscrazydaejeon.dto.response;

import com.daejeongwang.uoscrazydaejeon.entity.Receipt;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReceiptStatusResponse {
    private Long receiptId;
    private Long placeId;
    private String placeName;
    private Receipt.ReceiptStatus verifyStatus;
    private Receipt.OcrStatus ocrStatus;
    boolean gachaAvailable;
}
