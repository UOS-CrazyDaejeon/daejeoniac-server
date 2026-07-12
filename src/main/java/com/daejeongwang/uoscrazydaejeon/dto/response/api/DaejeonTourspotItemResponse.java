package com.daejeongwang.uoscrazydaejeon.dto.response.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DaejeonTourspotItemResponse {

    private String tourspotNm; // 관광지명

    private String tourspotZip; // 우편번호

    private String tourspotAddr; // 주소

    private String tourspotDtlAddr; // 상세주소

    private String refadNo; // 문의 전화번호

    private String mngTime; // 운영시간

    private String tourUtlzAmt; // 이용요금

    private String pkgFclt; // 주차시설

    private String cnvenFcltGuid; // 편의시설 안내

    private String urlAddr; // 홈페이지 URL

    private String tourspotSumm; // 관광지 소개

    private String mapLat; // 위도

    private String mapLot; // 경도
}
