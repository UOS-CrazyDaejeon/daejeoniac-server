package com.daejeongwang.uoscrazydaejeon.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RecommendationResponse {
    private String sessionId;
    private List<RecommendedPlaceResponse> recommendations;
}
