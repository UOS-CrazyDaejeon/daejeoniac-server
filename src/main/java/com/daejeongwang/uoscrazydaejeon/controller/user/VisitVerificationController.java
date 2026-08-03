package com.daejeongwang.uoscrazydaejeon.controller.user;

import com.daejeongwang.uoscrazydaejeon.config.SwaggerExamples;
import com.daejeongwang.uoscrazydaejeon.dto.ResultDto;
import com.daejeongwang.uoscrazydaejeon.dto.request.VisitVerificationRequest;
import com.daejeongwang.uoscrazydaejeon.dto.response.VisitVerificationResponse;
import com.daejeongwang.uoscrazydaejeon.service.VisitVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
@Tag(name = "Visit Verification", description = "방문 인증 API")
public class VisitVerificationController {
    private final VisitVerificationService visitVerificationService;

    @PostMapping("/{placeId}/visit-verifications")
    @Operation(summary = "방문 인증", description = "현재 로그인 된 사용자의 장소 방문을 인증합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "방문 인증 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 방문 인증 요청",
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
    public ResponseEntity<VisitVerificationResponse> verifyVisit(
            Authentication authentication,
            @PathVariable("placeId") Long placeId,
            @Valid @RequestBody VisitVerificationRequest request
    ) {
        Long memberId = Long.valueOf(authentication.getName());

        VisitVerificationResponse response = visitVerificationService.verifyVisit(memberId, placeId, request);

        return ResponseEntity.ok(response);
    }
}
