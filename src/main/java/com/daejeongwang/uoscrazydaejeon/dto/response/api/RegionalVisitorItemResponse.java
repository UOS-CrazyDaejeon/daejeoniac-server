package com.daejeongwang.uoscrazydaejeon.dto.response.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegionalVisitorItemResponse {
    private String signguCode; //시군구코드

    private String signguNm;  //시군구 명

    private String daywkDivCd;  //요일 구분 코드

    private String daywkDivNm;  //요일 구분 명

    private String touDivCd;  //관광객 구분 코드

    private String touDivNm;  //관광객 구분 명

    private BigDecimal touNum;  //관광객 수

    private String baseYmd;  //기준연월일
}
