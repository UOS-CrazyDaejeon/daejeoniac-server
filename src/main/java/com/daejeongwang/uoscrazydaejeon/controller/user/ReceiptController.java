package com.daejeongwang.uoscrazydaejeon.controller.user;

import com.daejeongwang.uoscrazydaejeon.config.SwaggerExamples;
import com.daejeongwang.uoscrazydaejeon.dto.ResultDto;
import com.daejeongwang.uoscrazydaejeon.dto.response.ReceiptResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.ReceiptStatusResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.ReceiptUploadUrlResponse;
import com.daejeongwang.uoscrazydaejeon.entity.Receipt;
import com.daejeongwang.uoscrazydaejeon.service.ReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/receipts")
@RequiredArgsConstructor
@Tag(name = "Receipt", description = "영수증 API")
public class ReceiptController {
    private final ReceiptService receiptService;

    @PostMapping("/upload-url")
    @Operation(summary = "영수증 업로드 URL 발급", description = "방문 기록에 대한 영수증 이미지 업로드 URL을 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "영수증 업로드 URL 발급 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 영수증 업로드 요청",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.BAD_REQUEST)
                    )),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class)
                    )),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<ReceiptUploadUrlResponse> createUploadUrl(
            Authentication authentication,
            @RequestParam Long visitedPlaceId,
            @RequestParam String contentType
    ) {
        Long memberId = Long.valueOf(authentication.getName());

        ReceiptUploadUrlResponse response = receiptService.issueUploadUrl(memberId, visitedPlaceId, contentType);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/ocr-result")
    @Operation(summary = "OCR 결과 저장", description = "영수증 OCR 처리 결과를 저장하고 승인 여부를 결정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "OCR 결과 저장 성공",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "잘못된 OCR 결과 요청",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.BAD_REQUEST)
                    )),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
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
    @Operation(summary = "영수증 상태 조회", description = "현재 로그인 된 사용자의 영수증 인증 상태를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "영수증 상태 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 영수증 상태 조회 요청",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.BAD_REQUEST)
                    )),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class)
                    )),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<ReceiptStatusResponse> getReceiptStatus(Authentication authentication, @PathVariable Long receiptId) {
        Long memberId = Long.valueOf(authentication.getName());

        ReceiptStatusResponse response = receiptService.getReceiptStatus(memberId, receiptId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "내 영수증 조회", description = "현재 로그인 된 사용자가 등록한 영수증을 조회합니다.")
    public ResponseEntity<List<ReceiptResponse>> getAllReceipt(Authentication authentication) {
        Long memberId = Long.valueOf(authentication.getName());

        List<ReceiptResponse> response = receiptService.getMyReceipts(memberId);

        return ResponseEntity.ok(response);
    }

}
