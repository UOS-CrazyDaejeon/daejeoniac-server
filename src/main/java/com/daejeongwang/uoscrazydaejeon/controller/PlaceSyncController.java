package com.daejeongwang.uoscrazydaejeon.controller;

import com.daejeongwang.uoscrazydaejeon.config.SwaggerExamples;
import com.daejeongwang.uoscrazydaejeon.dto.ResultDto;
import com.daejeongwang.uoscrazydaejeon.service.PlaceSearchService;
import com.daejeongwang.uoscrazydaejeon.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/sync")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "관리자 API")
public class PlaceSyncController {

    private final PlaceService placeService;
    private final PlaceSearchService placeSearchService;

    @PostMapping("/searched-places")
    @Operation(summary = "전체 장소 동기화", description = "대전의 모든 장소를 DB에 저장합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 장소 동기화 성공",
                    content = @Content(schema = @Schema(type = "string", example = "전체 장소 데이터 동기화 완료"))),
            @ApiResponse(responseCode = "500", description = "서버 오류 또는 외부 API 호출 실패",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<String> syncAllSearchedPlaces() {
        placeSearchService.syncPlaces();

        return ResponseEntity.ok("전체 장소 데이터 동기화 완료");
    }

    @PostMapping("/places")
    @Operation(summary = "전체 장소 동기화", description = "대전의 모든 장소를 DB에 저장합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 장소 동기화 성공",
                    content = @Content(schema = @Schema(type = "string", example = "전체 장소 데이터 동기화 완료"))),
            @ApiResponse(responseCode = "500", description = "서버 오류 또는 외부 API 호출 실패",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<String> syncAllPlaces() {
        placeService.syncAllPlaces();

        return ResponseEntity.ok("전체 장소 데이터 동기화 완료");
    }

    @PostMapping("/places/shopping")
    @Operation(summary = "쇼핑 장소 동기화", description = "대전의 쇼핑 관련 장소를 DB에 저장합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "쇼핑 장소 동기화 성공",
                    content = @Content(schema = @Schema(type = "string", example = "쇼핑 관련 장소 데이터 동기화 완료"))),
            @ApiResponse(responseCode = "500", description = "서버 오류 또는 외부 API 호출 실패",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<String> syncShoppingPlaces() {
        placeService.syncShoppingPlaces();

        return ResponseEntity.ok("쇼핑 관련 장소 데이터 동기화 완료");
    }

    @PostMapping("/places/tourspots")
    @Operation(summary = "관광지 장소 동기화", description = "대전의 관광지를 DB에 저장합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "관광지 장소 동기화 성공",
                    content = @Content(schema = @Schema(type = "string", example = "관광지 관련 장소 데이터 동기화 완료"))),
            @ApiResponse(responseCode = "500", description = "서버 오류 또는 외부 API 호출 실패",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<String> syncTourspotPlaces() {
        placeService.syncTourspotPlaces();

        return ResponseEntity.ok("관광지 관련 장소 데이터 동기화 완료");
    }

    @PostMapping("/places/restaurants")
    @Operation(summary = "일반 음식점 장소 동기화", description = "대전의 음식점을 DB에 저장합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "일반 음식점 장소 동기화 성공",
                    content = @Content(schema = @Schema(type = "string", example = "일반 음식점 관련 장소 데이터 동기화 완료"))),
            @ApiResponse(responseCode = "500", description = "서버 오류 또는 외부 API 호출 실패",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<String> syncRestaurantPlaces() {
        placeService.syncRestaurantPlaces();

        return ResponseEntity.ok("일반 음식점 관련 장소 데이터 동기화 완료");
    }
}
