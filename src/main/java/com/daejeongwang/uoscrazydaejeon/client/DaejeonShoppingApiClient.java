package com.daejeongwang.uoscrazydaejeon.client;

import com.daejeongwang.uoscrazydaejeon.dto.response.api.DaejeonShoppingApiResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.api.DaejeonShoppingItemResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Component
public class DaejeonShoppingApiClient {

    @Value("${daejeon.api.service-key}")
    private String serviceKey;
    private final String shoppingApiUrl = "https://www.data.go.kr/data/15000867/openapi.do#/API%20%EB%AA%A9%EB%A1%9D/getshppg";

    public List<DaejeonShoppingItemResponse> fetchShopping() {
        RestTemplate restTemplate = new RestTemplate();
        List<DaejeonShoppingItemResponse> allShoppingCenters = new ArrayList<>();

        String url = UriComponentsBuilder.fromUriString(shoppingApiUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("pageNo", "1")
                .queryParam("numOfRows", "100")
                .toUriString();

        DaejeonShoppingApiResponse response = restTemplate.getForObject(url, DaejeonShoppingApiResponse.class);

        if(response != null && response.getBody() != null && response.getBody().getItems() != null)
            allShoppingCenters.addAll(response.getBody().getItems().getItem());

        return allShoppingCenters;
    }
}
