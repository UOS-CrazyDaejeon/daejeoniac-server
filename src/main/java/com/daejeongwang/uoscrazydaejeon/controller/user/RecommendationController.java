package com.daejeongwang.uoscrazydaejeon.controller.user;

import com.daejeongwang.uoscrazydaejeon.dto.response.AiNextPlacesRecommendationResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.AiSimilarRecommendationResponse;
import com.daejeongwang.uoscrazydaejeon.exception.AuthenticationFailedException;
import com.daejeongwang.uoscrazydaejeon.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
@Tag(name = "Recommendation", description = "AI 추천 장소 API")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping("/similar-places")
    @Operation(summary = "선택 장소 기반 유사 장소 추천", description = "선택한 장소와 1km 이내 장소 정보를 AI 서버에 전달하여 유사 장소를 추천받습니다.")
    public ResponseEntity<AiSimilarRecommendationResponse> recommendSimilarPlaces(
            Authentication authentication,
            @RequestParam Long placeId
    ) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new AuthenticationFailedException("로그인이 필요한 요청입니다.");
        }

        Long memberId = Long.valueOf(authentication.getName());
        AiSimilarRecommendationResponse response = recommendationService.recommendSimilarPlaces(memberId, placeId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/next-places")
    @Operation(summary = "선택 장소와 방문 이력 기반 다음 장소 추천", description = "선택한 장소, 1km 이내 장소, 내 방문 장소 정보를 AI 서버에 전달하여 다음 장소를 추천받습니다.")
    public ResponseEntity<AiNextPlacesRecommendationResponse> recommendNextPlaces(
            Authentication authentication,
            @RequestParam Long placeId
    ) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new AuthenticationFailedException("로그인이 필요한 요청입니다.");
        }

        Long memberId = Long.valueOf(authentication.getName());
        AiNextPlacesRecommendationResponse response = recommendationService.recommendNextPlaces(memberId, placeId);

        return ResponseEntity.ok(response);
    }
}
