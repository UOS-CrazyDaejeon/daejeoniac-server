package com.daejeongwang.uoscrazydaejeon.client;

import com.daejeongwang.uoscrazydaejeon.dto.response.api.DaejeonTourspotApiResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.api.DaejeonTourspotItemResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Component
public class DaejeonTourspotApiClient {

    @Value("${daejeon.api.service-key}")
    private String serviceKey;
    private final String tourspotApiUrl = "https://www.data.go.kr/data/15000881/openapi.do#/API%20%EB%AA%A9%EB%A1%9D/gettourspot";

    public List<DaejeonTourspotItemResponse> fetchTourspot() {
        RestTemplate restTemplate = new RestTemplate();
        List<DaejeonTourspotItemResponse> allTourspots = new ArrayList<>();

        String url = UriComponentsBuilder.fromUriString(tourspotApiUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("pageNo", "1")
                .queryParam("numOfRows", "150")
                .toUriString();

        DaejeonTourspotApiResponse response = restTemplate.getForObject(url, DaejeonTourspotApiResponse.class);

        if(response != null && response.getBody() != null && response.getBody().getItems() != null)
            allTourspots.addAll(response.getBody().getItems().getItem());

        return allTourspots;
    }
}
