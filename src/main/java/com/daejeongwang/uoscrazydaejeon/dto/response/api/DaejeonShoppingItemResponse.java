package com.daejeongwang.uoscrazydaejeon.dto.response.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DaejeonShoppingItemResponse {

    private String shppgNm; // 쇼핑 장소명

    private String shppgZip; // 우편번호

    private String shppgAddr; // 주소

    private String shppgDtlAddr; // 상세주소

    private String shppgInqrTel; // 문의 전화번호

    private String pkgFclt; // 주차시설

    private String shppgHmpgUrl; // 홈페이지 Url

    private String salsTime; // 영업시간

    private String shppgIntrd; // 쇼핑 장소 소개

    private String mapLat; // 위도

    private String mapLot; // 경도
}
