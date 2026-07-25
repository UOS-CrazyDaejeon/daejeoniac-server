package com.daejeongwang.uoscrazydaejeon.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceSearchResponse {

    private String name;

    private String address;

    private String roadAddress;

    private String phoneNumber;

    private Double latitude;

    private Double longitude;

    private String category;

    private String url;
}
