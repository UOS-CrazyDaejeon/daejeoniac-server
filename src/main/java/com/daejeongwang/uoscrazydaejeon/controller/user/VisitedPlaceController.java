package com.daejeongwang.uoscrazydaejeon.controller.user;


import com.daejeongwang.uoscrazydaejeon.config.SwaggerExamples;
import com.daejeongwang.uoscrazydaejeon.dto.ResultDto;
import com.daejeongwang.uoscrazydaejeon.dto.response.VisitedPlaceListResponse;
import com.daejeongwang.uoscrazydaejeon.service.VisitedPlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/members/me/visited-places")
@RequiredArgsConstructor
@Tag(name = "Visited Place", description = "방문 장소 API")
public class VisitedPlaceController {
    private final VisitedPlaceService visitedPlaceService;

    @GetMapping
    @Operation(summary = "내 방문 장소 조회", description = "현재 로그인 된 사용자의 방문 장소 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내 방문 장소 조회 성공"),
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
    public ResponseEntity<List<VisitedPlaceListResponse>> getMyVisitedPlaces(Authentication authentication) {
        Long memberId = Long.valueOf(authentication.getName());

        List<VisitedPlaceListResponse> response = visitedPlaceService.getMyVisitedPlaces(memberId);

        return ResponseEntity.ok(response);
    }
}
