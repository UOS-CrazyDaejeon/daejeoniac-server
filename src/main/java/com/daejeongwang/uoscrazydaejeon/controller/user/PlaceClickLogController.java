package com.daejeongwang.uoscrazydaejeon.controller.user;

import com.daejeongwang.uoscrazydaejeon.config.SwaggerExamples;
import com.daejeongwang.uoscrazydaejeon.dto.ResultDto;
import com.daejeongwang.uoscrazydaejeon.service.PlaceClickLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
@Tag(name = "ClickLog", description = "장소 클릭 횟수 조회 API")
public class PlaceClickLogController {

    private final PlaceClickLogService placeClickLogService;

    // TODO : 현재 로그인 사용자의 ID를 파라미터로 넘겨주기
    @PostMapping("/{placeId}/clicks")
    @Operation(summary = "장소 별 클릭 로그 API", description = "장소가 클릭된 횟수를 증가시킵니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "클릭 로그 저장 성공",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "잘못된 장소 ID",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.BAD_REQUEST)
                    )),
            @ApiResponse(responseCode = "404", description = "장소를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.NOT_FOUND)
                    )),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<Void> saveClickLog(
            @PathVariable Long placeId
    ) {
        placeClickLogService.saveClickLog(placeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{placeId}/click-count")
    @Operation(summary = "장소 별 클릭 로그 조회 API", description = "특정 장소가 클릭된 횟수를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "클릭 횟수 조회 성공", content = @Content),
            @ApiResponse(responseCode = "400", description = "잘못된 장소 ID",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.BAD_REQUEST)
                    )),
            @ApiResponse(responseCode = "404", description = "장소를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.NOT_FOUND)
                    )),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<Long> getClickCount(@PathVariable Long placeId) {
        long count = placeClickLogService.getClickCountByPlaceId(placeId);

        return ResponseEntity.ok(count);
    }
}
