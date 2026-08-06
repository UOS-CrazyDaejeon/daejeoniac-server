package com.daejeongwang.uoscrazydaejeon.dto.response;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class PlacePhotoUploadUrlResponse {

    private Long placePhotoId;

    private String uploadUrl;

    private Integer expiresIn;
}


