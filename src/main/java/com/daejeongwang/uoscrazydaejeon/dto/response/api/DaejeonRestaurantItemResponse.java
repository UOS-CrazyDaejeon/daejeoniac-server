package com.daejeongwang.uoscrazydaejeon.dto.response.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DaejeonRestaurantItemResponse {

    @JsonProperty("소재지(도로명)")
    private String roadAddress; // 도로명 주소

    @JsonProperty("소재지전화")
    private String phoneNumber; // 전화번호

    @JsonProperty("업소명")
    private String restaurantName; // 음식점명

    @JsonProperty("업종명")
    private String businessType; // 업종명
}
