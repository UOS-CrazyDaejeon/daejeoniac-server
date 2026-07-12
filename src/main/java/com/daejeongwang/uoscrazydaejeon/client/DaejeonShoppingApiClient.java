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
    private final String shoppingApiUrl = "https://apis.data.go.kr/6300000/openapi2022/shppg/getshppg";

    public List<DaejeonShoppingItemResponse> fetchShopping() {
        RestTemplate restTemplate = new RestTemplate();
        List<DaejeonShoppingItemResponse> allShoppingCenters = new ArrayList<>();

        String url = UriComponentsBuilder.fromUriString(shoppingApiUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("pageNo", "1")
                .queryParam("numOfRows", "100")
                .toUriString();

        DaejeonShoppingApiResponse response = restTemplate.getForObject(url, DaejeonShoppingApiResponse.class);

        if (response != null
                && response.getResponse() != null
                && response.getResponse().getBody() != null
                && response.getResponse().getBody().getItems() != null) {
            allShoppingCenters.addAll(response.getResponse().getBody().getItems());
        }

        return allShoppingCenters;
    }
}
