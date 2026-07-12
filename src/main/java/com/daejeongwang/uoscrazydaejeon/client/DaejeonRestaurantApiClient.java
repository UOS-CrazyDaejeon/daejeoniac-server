package com.daejeongwang.uoscrazydaejeon.client;

import com.daejeongwang.uoscrazydaejeon.dto.response.api.DaejeonRestaurantApiResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.api.DaejeonRestaurantItemResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.api.DaejeonShoppingApiResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.api.DaejeonShoppingItemResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Component
public class DaejeonRestaurantApiClient {

    @Value("${daejeon.api.service-key}")
    private String serviceKey;
    private final String RestaurantApiUrl = "https://api.odcloud.kr/api/15008957/v1/uddi:b86ea889-fe38-428a-a51b-ac0dfb92dd97";

    public List<DaejeonRestaurantItemResponse> fetchRestaurants() {
        RestTemplate restTemplate = new RestTemplate();
        List<DaejeonRestaurantItemResponse> allRestaurants = new ArrayList<>();

        String url = UriComponentsBuilder.fromUriString(RestaurantApiUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("page", 1)
                .queryParam("perPage", 100)
                .toUriString();

        DaejeonRestaurantApiResponse response = restTemplate.getForObject(url, DaejeonRestaurantApiResponse.class);

        if (response != null && response.getData() != null)
            allRestaurants.addAll(response.getData());

        return allRestaurants;
    }
}
