package com.daejeongwang.uoscrazydaejeon.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.daejeongwang.uoscrazydaejeon.dto.response.KakaoResponse;
import com.daejeongwang.uoscrazydaejeon.exception.KakaoApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@Slf4j
public class KakaoUtil {

    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.redirect-url}")
    private String kakaoRedirectUrl;

    @Value("${kakao.client-secret}")
    private String kakaoClientSecret;


    ObjectMapper objectMapper = new ObjectMapper();

    public KakaoResponse.OAuthToken parseKakaoToken(String responseBody) {
        try {
            return objectMapper.readValue(responseBody, KakaoResponse.OAuthToken.class);
        } catch (JsonProcessingException e) {
            throw new KakaoApiException(HttpStatus.BAD_GATEWAY, "카카오 토큰 응답 파싱 실패");
        }
    }

    public KakaoResponse.OAuthToken requestKakaoToken(String kakaoAuthorizationCode) {
        try {
            String responseBody = WebClient.create("https://kauth.kakao.com")
                    .post()
                    .uri("/oauth/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                            .with("client_id", kakaoClientId)
                            .with("redirect_uri", kakaoRedirectUrl)
                            .with("code", kakaoAuthorizationCode)
                            .with("client_secret", kakaoClientSecret))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseKakaoToken(responseBody);

        } catch (WebClientResponseException e) {
            throw new KakaoApiException(
                    HttpStatus.valueOf(e.getStatusCode().value()),
                    "카카오 토큰 요청 실패: " + e.getResponseBodyAsString()
            );
        }
    }

    public KakaoResponse.KakaoProfile requestKakaoProfile(String kakaoAccessToken) {
        try {
            String responseBody = WebClient.create("https://kapi.kakao.com")
                    .get()
                    .uri("/v2/user/me")
                    .header("Authorization", "Bearer " + kakaoAccessToken)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return objectMapper.readValue(responseBody, KakaoResponse.KakaoProfile.class);

        } catch (WebClientResponseException e) {
            throw new KakaoApiException(
                    HttpStatus.valueOf(e.getStatusCode().value()),
                    "카카오 사용자 정보 요청 실패: " + e.getResponseBodyAsString()
            );
        } catch (JsonProcessingException e) {
            throw new KakaoApiException(HttpStatus.BAD_GATEWAY, "카카오 사용자 정보 응답 파싱 실패");
        }
    }
}
