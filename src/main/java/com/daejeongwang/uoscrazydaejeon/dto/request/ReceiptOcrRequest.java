package com.daejeongwang.uoscrazydaejeon.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptOcrRequest {
    private UUID receiptUuid;
    private String objectKey;
}
