package com.daejeongwang.uoscrazydaejeon.controller.admin;

import com.daejeongwang.uoscrazydaejeon.config.SwaggerExamples;
import com.daejeongwang.uoscrazydaejeon.dto.ResultDto;
import com.daejeongwang.uoscrazydaejeon.service.CongestionService;
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
@Tag(name = "Admin-Congestion", description = "관리자 전용 혼잡도 관리 API")
public class AdminCongestionSyncController {

    private final CongestionService congestionService;

    @PostMapping("/congestion")
    @Operation(summary = "장소 예상 혼잡도 저장", description = "각 장소에 대한 날짜 별 예상 혼잡도를 DB에 저장합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "장소 예상 혼잡도 동기화 성공",
                    content = @Content(schema = @Schema(type = "string", example = "각 장소 혼잡도 동기화 완료"))),
            @ApiResponse(responseCode = "500", description = "서버 오류 또는 외부 API 호출 실패",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<String> syncAllPlaces() {
        congestionService.syncCongestions();

        return ResponseEntity.ok("각 장소 혼잡도 동기화 완료");
    }
}
