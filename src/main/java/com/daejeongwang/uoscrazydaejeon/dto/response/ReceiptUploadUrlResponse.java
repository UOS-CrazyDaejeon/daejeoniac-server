package com.daejeongwang.uoscrazydaejeon.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ReceiptUploadUrlResponse {

    private Long receiptId;

    private String uploadUrl;

    private Integer expiresIn;
}

