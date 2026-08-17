package com.daejeongwang.uoscrazydaejeon.controller.user;

import com.daejeongwang.uoscrazydaejeon.dto.RecommendationSession;
import com.daejeongwang.uoscrazydaejeon.dto.request.RecommendationSessionRequest;
import com.daejeongwang.uoscrazydaejeon.service.RecommendationSessionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/recommendation-sessions")
@RequiredArgsConstructor
@Tag(name = "Recommendation Session", description = "추천 장소 세션 저장/조회 API")
public class RecommendationSessionController {
    private final RecommendationSessionService recommendationSessionService;

    @PostMapping
    public ResponseEntity<String> saveSession(
            Authentication authentication,
            @Valid @RequestBody RecommendationSessionRequest request
    ) {
        Long memberId = Long.valueOf(authentication.getName());

        RecommendationSession session = RecommendationSession.builder()
                .memberId(memberId)
                .recommendations(request.getRecommendations())
                .createdAt(LocalDateTime.now())
                .build();

        String sessionId = recommendationSessionService.saveSession(session);

        return ResponseEntity.status(HttpStatus.CREATED).body(sessionId);
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<RecommendationSession> getSession(
            Authentication authentication,
            @PathVariable String sessionId
    ) {
        Long memberId = Long.valueOf(authentication.getName());

        RecommendationSession session =
                recommendationSessionService.getSession(memberId, sessionId);

        if (session == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(session);
    }
}
