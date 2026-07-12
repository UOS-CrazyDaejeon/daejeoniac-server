package com.daejeongwang.uoscrazydaejeon.client;

import com.daejeongwang.uoscrazydaejeon.dto.response.api.CongestionApiResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.api.CongestionItemResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Component
public class CongestionApiClient {

    private final String areaCd = "30"; // 대전
    private final List<String> sigunguCodes = List.of(
            "30110", // 동구
            "30140", // 중구
            "30170", // 서구
            "30200", // 유성구
            "30230"  // 대덕구
    );

    @Value("${daejeon.api.service-key}")
    private String serviceKey;
    private final String trendApiUrl = "https://apis.data.go.kr/B551011/TatsCnctrRateService/tatsCnctrRatedList";

    public List<CongestionItemResponse> fetchAllCongestions() {
        List<CongestionItemResponse> allTrends = new ArrayList<>();

        for (String signguCd : sigunguCodes) {
            List<CongestionItemResponse> trends = fetchCongestionBySigungu(signguCd);
            allTrends.addAll(trends);
        }

        return allTrends;
    }

    public List<CongestionItemResponse> fetchCongestionBySigungu(String signguCd) {
        RestTemplate restTemplate = new RestTemplate();
        List<CongestionItemResponse> trend = new ArrayList<>();

        String url = UriComponentsBuilder.fromUriString(trendApiUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 100)
                .queryParam("MobileOS", "ETC")
                .queryParam("MobileApp", "UosCrazyDaejeon")
                .queryParam("_type", "json")
                .queryParam("areaCd", areaCd)
                .queryParam("signguCd", signguCd)
                .toUriString();

        CongestionApiResponse response = restTemplate.getForObject(url, CongestionApiResponse.class);

        if(response != null
                && response.getResponse() != null
                && response.getResponse().getBody() != null
                && response.getResponse().getBody().getItems() != null
                && response.getResponse().getBody().getItems().getItem() != null)
            trend.addAll(response.getResponse().getBody().getItems().getItem());

        return trend;
    }
}
