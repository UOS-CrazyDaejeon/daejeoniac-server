package com.daejeongwang.uoscrazydaejeon.dto;

import com.daejeongwang.uoscrazydaejeon.dto.response.RecommendedPlaceResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationSession {
    private Long memberId;
    private Long parentPlaceId;
    private List<RecommendedPlaceResponse> recommendations;
    private LocalDateTime createdAt;
}
