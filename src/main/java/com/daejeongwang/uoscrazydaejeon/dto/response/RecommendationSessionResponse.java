package com.daejeongwang.uoscrazydaejeon.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RecommendationSessionResponse(
        UUID sessionId,
        Long parentPlaceId,
        LocalDateTime createdAt,
        List<RecommendationSessionPlaceResponse> recommendations
) {
}
