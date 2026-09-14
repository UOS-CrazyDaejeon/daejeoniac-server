package com.daejeongwang.uoscrazydaejeon.controller.user;

import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import com.daejeongwang.uoscrazydaejeon.dto.ResultDto;
import com.daejeongwang.uoscrazydaejeon.config.SwaggerExamples;
import com.daejeongwang.uoscrazydaejeon.dto.response.RecommendationSessionResponse;
import com.daejeongwang.uoscrazydaejeon.service.RecommendationSessionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/recommendation-sessions")
@RequiredArgsConstructor
@Tag(name = "Recommendation Session", description = "추천 장소 세션 저장/조회 API")
public class RecommendationSessionController {
    private final RecommendationSessionService recommendationSessionService;

    @GetMapping("/{sessionId}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추천 세션 조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.BAD_REQUEST))),
            @ApiResponse(responseCode = "404", description = "세션이 없거나 만료되었거나 본인 세션이 아님", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)))
    })
    public ResponseEntity<RecommendationSessionResponse> getSession(
            Authentication authentication,
            @PathVariable UUID sessionId
    ) {
        Long memberId = Long.valueOf(authentication.getName());

        RecommendationSessionResponse session =
                recommendationSessionService.getSession(memberId, sessionId);

        if (session == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(session);
    }
}
