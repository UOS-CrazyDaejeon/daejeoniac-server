package com.daejeongwang.uoscrazydaejeon.controller.user;

import com.daejeongwang.uoscrazydaejeon.config.SwaggerExamples;
import com.daejeongwang.uoscrazydaejeon.dto.ResultDto;
import com.daejeongwang.uoscrazydaejeon.dto.response.PlaceSearchResponse;
import com.daejeongwang.uoscrazydaejeon.service.PlaceSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
@Tag(name = "Place Search", description = "장소 검색 API")
public class MapController {

    private final PlaceSearchService placeSearchService;

    @GetMapping("/search")
    @Operation(summary = "장소 검색", description = "실시간 호출하여 장소 검색 결과를 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "장소 검색 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 검색어",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.BAD_REQUEST)
                    )),
            @ApiResponse(responseCode = "500", description = "서버 오류 또는 외부 API 호출 실패",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<List<PlaceSearchResponse>> searchPlaces(@RequestParam String keyword) {
        List<PlaceSearchResponse> result = placeSearchService.searchPlaces(keyword);

        return ResponseEntity.ok(result);
    }
}
