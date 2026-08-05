package com.daejeongwang.uoscrazydaejeon.controller.user;

import com.daejeongwang.uoscrazydaejeon.config.SwaggerExamples;
import com.daejeongwang.uoscrazydaejeon.dto.ResultDto;
import com.daejeongwang.uoscrazydaejeon.dto.request.PlacePhotoUploadUrlRequest;
import com.daejeongwang.uoscrazydaejeon.dto.response.PlacePhotoByPlaceResponse;
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

import java.util.List;

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

    @PatchMapping("/{placePhotoId}/complete")
    @Operation(summary = "장소 사진 업로드 완료", description = "S3 업로드 여부를 확인한 뒤 장소 사진 업로드를 완료 처리합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "장소 사진 업로드 완료 처리 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class)
                    )),
            @ApiResponse(responseCode = "404", description = "장소 사진을 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class))),
            @ApiResponse(responseCode = "409", description = "S3에 업로드된 사진 객체를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class)
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<Void> completeUpload(
            Authentication authentication,
            @PathVariable Long placePhotoId
    ) {
        Long memberId = Long.valueOf(authentication.getName());

        placePhotoService.completeUpload(memberId, placePhotoId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{placeId}")
    @Operation(summary = "장소별 사진 목록 조회", description = "해당 장소에 등록된 사진을 최신순으로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "장소별 사진 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "장소를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<List<PlacePhotoByPlaceResponse>> getPlacePhotosByPlaceId(
            @PathVariable Long placeId
    ) {
        List<PlacePhotoByPlaceResponse> responses = placePhotoService.getPlacePhotosByPlace(placeId);

        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }


}
