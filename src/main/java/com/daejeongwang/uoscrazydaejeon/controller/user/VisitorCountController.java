package com.daejeongwang.uoscrazydaejeon.controller.user;

import com.daejeongwang.uoscrazydaejeon.config.SwaggerExamples;
import com.daejeongwang.uoscrazydaejeon.dto.ResultDto;
import com.daejeongwang.uoscrazydaejeon.dto.response.VisitorCountResponse;
import com.daejeongwang.uoscrazydaejeon.service.VisitorCountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/visitor-count")
@RequiredArgsConstructor
@Tag(name = "Visitor Count", description = "방문자 수 조회 API")
public class VisitorCountController {

    private final VisitorCountService visitorCountService;

    @GetMapping("/{placeId}")
    @Operation(summary = "장소별 방문자 수 조회", description = "해당 장소의 방문자 수를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "방문자 수 조회 성공"),
            @ApiResponse(responseCode = "404", description = "장소를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<VisitorCountResponse> getVisitorCountByPlace(
            @PathVariable Long placeId
    ) {
        VisitorCountResponse response = visitorCountService.getVisitorCountByPlace(placeId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
