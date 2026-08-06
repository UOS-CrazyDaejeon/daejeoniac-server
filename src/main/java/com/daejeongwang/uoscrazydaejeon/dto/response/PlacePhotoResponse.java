package com.daejeongwang.uoscrazydaejeon.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PlacePhotoResponse {
    private Long placePhotoId;
    private Long placeId;
    private String placeName;
    private String imageUrl;
    private LocalDateTime createdAt;
}
