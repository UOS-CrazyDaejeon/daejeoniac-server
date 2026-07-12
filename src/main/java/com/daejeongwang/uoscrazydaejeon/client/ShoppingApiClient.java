package com.daejeongwang.uoscrazydaejeon.client;

import com.daejeongwang.uoscrazydaejeon.dto.response.api.ShoppingApiResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.api.ShoppingItemResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Component
public class ShoppingApiClient {

    @Value("${daejeon.api.service-key}")
    private String serviceKey;
    private final String shoppingApiUrl = "https://apis.data.go.kr/6300000/openapi2022/shppg/getshppg";

    public List<ShoppingItemResponse> fetchShopping() {
        RestTemplate restTemplate = new RestTemplate();
        List<ShoppingItemResponse> allShoppingCenters = new ArrayList<>();

        String url = UriComponentsBuilder.fromUriString(shoppingApiUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("pageNo", "1")
                .queryParam("numOfRows", "100")
                .toUriString();

        ShoppingApiResponse response = restTemplate.getForObject(url, ShoppingApiResponse.class);

        if (response != null
                && response.getResponse() != null
                && response.getResponse().getBody() != null
                && response.getResponse().getBody().getItems() != null) {
            allShoppingCenters.addAll(response.getResponse().getBody().getItems());
        }

        return allShoppingCenters;
    }
}
