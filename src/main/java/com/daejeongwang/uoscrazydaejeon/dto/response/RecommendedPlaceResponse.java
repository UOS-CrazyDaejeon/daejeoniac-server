package com.daejeongwang.uoscrazydaejeon.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedPlaceResponse {
    private Long placeId;
    private String reason;
}
