package com.daejeongwang.uoscrazydaejeon.dto.response.api;

import com.daejeongwang.uoscrazydaejeon.entity.Receipt;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptOcrResultResponse {
    @NotNull
    private UUID receiptUuid;

    @NotNull
    private Receipt.OcrStatus ocrStatus;

    private String ocrPlaceName;
    private String ocrPlaceAddress;
    private LocalDateTime ocrPaidAt;
}
