package com.daejeongwang.uoscrazydaejeon.client;

import com.daejeongwang.uoscrazydaejeon.dto.response.api.RestaurantApiResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.api.RestaurantItemResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Component
public class RestaurantApiClient {

    @Value("${daejeon.api.service-key}")
    private String serviceKey;
    private final String restaurantApiUrl = "https://api.odcloud.kr/api/15008957/v1/uddi:b86ea889-fe38-428a-a51b-ac0dfb92dd97";

    public List<RestaurantItemResponse> fetchRestaurants() {
        RestTemplate restTemplate = new RestTemplate();
        List<RestaurantItemResponse> allRestaurants = new ArrayList<>();

        String url = UriComponentsBuilder.fromUriString(restaurantApiUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("page", 1)
                .queryParam("perPage", 100)
                .toUriString();

        RestaurantApiResponse response = restTemplate.getForObject(url, RestaurantApiResponse.class);

        if (response != null && response.getData() != null)
            allRestaurants.addAll(response.getData());

        return allRestaurants;
    }
}
