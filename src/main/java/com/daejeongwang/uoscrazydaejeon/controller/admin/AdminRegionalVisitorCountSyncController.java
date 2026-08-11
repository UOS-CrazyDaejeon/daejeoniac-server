package com.daejeongwang.uoscrazydaejeon.controller.admin;

import com.daejeongwang.uoscrazydaejeon.config.SwaggerExamples;
import com.daejeongwang.uoscrazydaejeon.dto.ResultDto;
import com.daejeongwang.uoscrazydaejeon.service.RegionalVisitorCountService;
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

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/admin/sync")
@RequiredArgsConstructor
@Tag(name = "Admin-Regional-VisitorCount", description = "관리자 전용 지역 방문자 수 관리 API")

public class AdminRegionalVisitorCountSyncController {
    private final RegionalVisitorCountService regionalVisitorCountService;

    @PostMapping("/regional-visitor-count/all")
    @Operation(summary = "지역 방문자 수 전체 동기화", description = "한국관광공사 지역별 방문자 수 API에서 지정한 기간의 대전 지역 방문자 수를 조회하여 DB에 저장하거나 갱신합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "지역 방문자 수 전체 동기화 성공",
                    content = @Content(schema = @Schema(type = "string", example = "기간 내 전체 방문자 수 동기화 완료"))),
            @ApiResponse(responseCode = "500", description = "서버 오류 또는 외부 API 호출 실패",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<String> syncAllVisitorCounts(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        regionalVisitorCountService.syncAllVisitorCounts(startDate, endDate);
        return ResponseEntity.ok("기간 내 전체 방문자 수 동기화 완료");
    }

    @PostMapping("/regional-visitor-count/latest")
    @Operation(summary = "최신 지역 방문자 수 동기화", description = "DB에 저장된 마지막 방문자 수 날짜 이후 새로 제공된 대전 지역 방문자 수를 조회하여 저장하거나 갱신합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "최신 지역 방문자 수 동기화 성공",
                    content = @Content(schema = @Schema(type = "string", example = "최신 방문자 수 동기화 완료"))),
            @ApiResponse(responseCode = "500", description = "서버 오류 또는 외부 API 호출 실패",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<String> syncLatestVisitorCounts() {
        regionalVisitorCountService.syncLatestVisitorCounts();
        return ResponseEntity.ok("최신 방문자 수 동기화 완료");
    }
}
