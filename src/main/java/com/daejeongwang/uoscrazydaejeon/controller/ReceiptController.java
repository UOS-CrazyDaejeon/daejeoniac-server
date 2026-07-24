package com.daejeongwang.uoscrazydaejeon.controller;

import com.daejeongwang.uoscrazydaejeon.dto.response.ReceiptUploadUrlResponse;
import com.daejeongwang.uoscrazydaejeon.service.ReceiptService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/receipts")
@RequiredArgsConstructor
@Tag(name = "Receipt", description = "영수증 API")
public class ReceiptController {
    private final ReceiptService receiptService;

    @PostMapping("/upload-url")
    public ResponseEntity<ReceiptUploadUrlResponse> createUploadUrl(@RequestParam Long placeId, @RequestParam String contentType) {
        ReceiptUploadUrlResponse response = receiptService.issueUploadUrl(placeId, contentType);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
