package com.daejeongwang.uoscrazydaejeon.dto.response;

public record RecommendationSessionPlaceResponse(
        Long placeId,
        String placeName,
        String tag,
        Long viewerCount,
        String reason
) {
}
