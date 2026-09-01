package com.daejeongwang.uoscrazydaejeon.controller.user;

import com.daejeongwang.uoscrazydaejeon.config.SwaggerExamples;
import com.daejeongwang.uoscrazydaejeon.dto.ResultDto;
import com.daejeongwang.uoscrazydaejeon.dto.request.PlacePhotoUploadRequest;
import com.daejeongwang.uoscrazydaejeon.dto.response.PlacePhotoByPlaceResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.PlacePhotoResponse;
import com.daejeongwang.uoscrazydaejeon.service.PlacePhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/place-photos")
@RequiredArgsConstructor
@Tag(name = "Place Photo", description = "실시간 장소 사진 API")
public class PlacePhotoController {
    private final PlacePhotoService placePhotoService;

    @PostMapping(
            value = "{placeId}/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(summary = "실시간 장소 사진 블러처리 및 업로드", description = "장소 근처에서 촬영한 실시간 사진을 블러처리하고 저장합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "장소 사진 저장 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
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
    public ResponseEntity<PlacePhotoResponse> uploadPlacePhoto(
            Authentication authentication,
            @PathVariable Long placeId,
            @RequestPart("image") MultipartFile image,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            encoding = @Encoding(
                                    name = "request",
                                    contentType = MediaType.APPLICATION_JSON_VALUE
                            )
                    )
            )
            @Valid @RequestPart("request") PlacePhotoUploadRequest request
    ) {
        Long memberId = Long.valueOf(authentication.getName());

        PlacePhotoResponse response = placePhotoService.uploadPlacePhoto(memberId, placeId, image, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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

    @GetMapping("/me")
    @Operation(summary = "내 장소 사진 조회", description = "현재 로그인 된 사용자가 등록한 장소사진을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내 장소 사진 조회 성공"),
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
    public ResponseEntity<List<PlacePhotoResponse>> getMyPlacePhotos(
            Authentication authentication
    ) {
        Long memberId = Long.valueOf(authentication.getName());
        List<PlacePhotoResponse> responses = placePhotoService.getMyPlacePhotos(memberId);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @DeleteMapping("/{placePhotoId}")
    @Operation(summary = "장소 사진 삭제", description = "현재 로그인 된 사용자가 올린 장소 사진을 S3와 DB에서 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "장소 사진 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class)
                    )),
            @ApiResponse(responseCode = "404", description = "장소 사진이 없거나 본인이 등록한 사진이 아님",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class)
                    )),
            @ApiResponse(responseCode = "500", description = "서버 오류 또는 S3 삭제 실패",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<Void> deleteMyPlacePhoto(
            Authentication authentication,
            @PathVariable Long placePhotoId
    ) {
        Long memberId = Long.valueOf(authentication.getName());
        placePhotoService.deletePlacePhoto(memberId, placePhotoId);
        return ResponseEntity.noContent().build();
    }

}
