package com.daejeongwang.uoscrazydaejeon.controller.user;

import com.daejeongwang.uoscrazydaejeon.config.SwaggerExamples;
import com.daejeongwang.uoscrazydaejeon.dto.ResultDto;
import com.daejeongwang.uoscrazydaejeon.dto.request.PlacePhotoUploadUrlRequest;
import com.daejeongwang.uoscrazydaejeon.dto.response.PlacePhotoUploadUrlResponse;
import com.daejeongwang.uoscrazydaejeon.service.PlacePhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/place-photos")
@RequiredArgsConstructor
@Tag(name = "Place Photo", description = "실시간 장소 사진 API")
public class PlacePhotoController {
    private final PlacePhotoService placePhotoService;

    @PostMapping("/{placeId}/upload-url")
    @Operation(summary = "실시간 장소 사진 업로드 URL 발급", description = "장소 근처에서 촬영한 실시간 사진의 업로드 URL을 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "장소 사진 업로드 URL 발급 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 장소 사진 업로드 요청",
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
    public ResponseEntity<PlacePhotoUploadUrlResponse> createUploadUrl(
            Authentication authentication,
            @PathVariable Long placeId,
            @RequestBody PlacePhotoUploadUrlRequest request
    ) {
        Long memberId = Long.valueOf(authentication.getName());

        PlacePhotoUploadUrlResponse response =
                placePhotoService.issueUploadUrl(memberId, placeId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
