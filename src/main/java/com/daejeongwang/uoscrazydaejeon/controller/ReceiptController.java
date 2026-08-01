package com.daejeongwang.uoscrazydaejeon.controller;

import com.daejeongwang.uoscrazydaejeon.dto.response.ReceiptStatusResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.ReceiptUploadUrlResponse;
import com.daejeongwang.uoscrazydaejeon.entity.Receipt;
import com.daejeongwang.uoscrazydaejeon.service.ReceiptService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/receipts")
@RequiredArgsConstructor
@Tag(name = "Receipt", description = "영수증 API")
public class ReceiptController {
    private final ReceiptService receiptService;

    @PostMapping("/upload-url")
    public ResponseEntity<ReceiptUploadUrlResponse> createUploadUrl(
            @RequestParam Long visitedPlaceId,
            @RequestParam String contentType
    ) {
        ReceiptUploadUrlResponse response = receiptService.issueUploadUrl(visitedPlaceId, contentType);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/ocr-result")
    public ResponseEntity<Void> saveOcrResult(
            @RequestParam UUID receiptUuid,
            @RequestParam Receipt.OcrStatus ocrStatus,
            @RequestParam(required = false) String ocrPlaceName,
            @RequestParam(required = false) String ocrPlaceAddress,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ocrPaidAt
    ) {
        receiptService.saveOcrResult(receiptUuid, ocrStatus, ocrPlaceName, ocrPlaceAddress, ocrPaidAt);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{receiptId}/status")
    public ResponseEntity<ReceiptStatusResponse> getReceiptStatus(@PathVariable Long receiptId) {
        ReceiptStatusResponse response = receiptService.getReceiptStatus(receiptId);

        return ResponseEntity.ok(response);
    }

}
