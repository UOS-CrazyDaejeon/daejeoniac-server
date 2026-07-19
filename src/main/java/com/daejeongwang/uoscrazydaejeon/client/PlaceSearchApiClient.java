package com.daejeongwang.uoscrazydaejeon.client;

import com.daejeongwang.uoscrazydaejeon.dto.response.api.PlaceSearchApiResponse;
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
public class PlaceSearchApiClient {

    @Value("${kakao.api.rest-key}")
    private String restApiKey;

    @Value("${kakao.api.keyword-url}")
    private String keywordUrl;

    public PlaceSearchApiResponse searchByKeyword(String keyword, Integer page, Integer size) {
        RestTemplate restTemplate = new RestTemplate();

        URI uri = UriComponentsBuilder.fromUriString(keywordUrl)
                .queryParam("query", keyword)
                .queryParam("page", page)
                .queryParam("size", size)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + restApiKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // header를 넣어서 GET 요청을 보내줘어야함. exchange 사용
        ResponseEntity<PlaceSearchApiResponse> response =
                restTemplate.exchange(
                        uri,
                        HttpMethod.GET,
                        entity,
                        PlaceSearchApiResponse.class
                );

        return response.getBody();
    }
}
