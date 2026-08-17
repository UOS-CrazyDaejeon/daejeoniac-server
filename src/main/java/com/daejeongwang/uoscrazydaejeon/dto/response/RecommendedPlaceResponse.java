package com.daejeongwang.uoscrazydaejeon.dto.response;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedPlaceResponse {
    @NotNull
    private Long placeId;
    private String reason;
}
