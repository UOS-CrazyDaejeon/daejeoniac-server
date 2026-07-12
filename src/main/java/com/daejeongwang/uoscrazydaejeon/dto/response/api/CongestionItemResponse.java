package com.daejeongwang.uoscrazydaejeon.dto.response.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CongestionItemResponse {

    private String cnctrRate; // 혼잡도 비율

    private String baseYmd; // 기준 날짜

    private String areaCd; // 시도 코드

    private String areaNm; // 시도명

    private String signguCd; // 시군구 코드

    private String signguNm; // 시군구명

    private String tAtsNm; // 관광지명

}
