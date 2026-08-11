package com.daejeongwang.uoscrazydaejeon.controller.user;

import com.daejeongwang.uoscrazydaejeon.config.SwaggerExamples;
import com.daejeongwang.uoscrazydaejeon.dto.ResultDto;
import com.daejeongwang.uoscrazydaejeon.dto.response.RegionalVisitorCountResponse;
import com.daejeongwang.uoscrazydaejeon.service.RegionalVisitorCountService;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/regional-visitor-count")
@RequiredArgsConstructor
@Tag(name = "Regional Visitor Count", description = "지역 방문자 수 조회 API")
public class RegionalVisitorCountController {
    private final RegionalVisitorCountService regionalVisitorCountService;

    @GetMapping()
    @Operation(summary = "날짜별 방문자 수 조회", description = "해당 날짜의 지역 방문자 수를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "방문자 수 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<RegionalVisitorCountResponse> getVisitorCountsByDate(
            @RequestParam LocalDate date
    ) {
        RegionalVisitorCountResponse response = regionalVisitorCountService.getVisitorCountsByDate(date);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
