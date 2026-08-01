package com.daejeongwang.uoscrazydaejeon.client;

import com.daejeongwang.uoscrazydaejeon.dto.response.api.AddressApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Component
public class AddressApiClient {

    @Value("${kakao.api.rest-key}")
    private String restApiKey;
    private final String addressApiUrl = "https://dapi.kakao.com/v2/local/search/address";

    public AddressApiResponse searchCoordinateByAddress(String address) {
        RestTemplate restTemplate = new RestTemplate();

        URI uri = UriComponentsBuilder.fromUriString(addressApiUrl)
                .queryParam("query", address)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + restApiKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<AddressApiResponse> response =
                restTemplate.exchange(
                        uri,
                        HttpMethod.GET,
                        entity,
                        AddressApiResponse.class
                );

        return response.getBody();
    }

}
