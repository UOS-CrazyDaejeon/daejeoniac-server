package com.daejeongwang.uoscrazydaejeon.client;

import com.daejeongwang.uoscrazydaejeon.dto.request.ReceiptOcrRequest;
import com.daejeongwang.uoscrazydaejeon.dto.request.NextPlacesRecommendationRequest;
import com.daejeongwang.uoscrazydaejeon.dto.request.SimilarRecommendationRequest;
import com.daejeongwang.uoscrazydaejeon.dto.response.AiNextPlacesRecommendationResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.AiSimilarRecommendationResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.api.ReceiptOcrResultResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Component
@Slf4j
public class AiServerClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${ai.server.url}")
    private String aiServerUrl;

    public AiServerClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        //AI server connect timeout
        factory.setConnectTimeout(Duration.ofSeconds(3));
        //AI server response timeout
        factory.setReadTimeout(Duration.ofSeconds(30));

        this.restTemplate = new RestTemplate(factory);
    }

    public ReceiptOcrResultResponse requestOcr(UUID receiptUuid, String objectKey) {

        ReceiptOcrRequest request =
                new ReceiptOcrRequest(receiptUuid, objectKey);

        return restTemplate.postForObject(
                aiServerUrl + "/api/v1/ocr",
                request,
                ReceiptOcrResultResponse.class
        );
    }

    public AiSimilarRecommendationResponse requestSimilarRecommendations(SimilarRecommendationRequest request) {
        String url = aiServerUrl + "/api/v1/recommendations/similar-places";

        logAiRequest(url, request);

        try {
            return restTemplate.postForObject(
                    url,
                    createJsonEntity(request),
                    AiSimilarRecommendationResponse.class
            );
        } catch (RestClientResponseException e) {
            logAiError("similar recommendation", request, e);
            throw e;
        }
    }

    public AiNextPlacesRecommendationResponse requestNextPlacesRecommendations(NextPlacesRecommendationRequest request) {
        String url = aiServerUrl + "/api/v1/recommendations/next-places";

        logAiRequest(url, request);

        try {
            return restTemplate.postForObject(
                    url,
                    createJsonEntity(request),
                    AiNextPlacesRecommendationResponse.class
            );
        } catch (RestClientResponseException e) {
            logAiError("next places recommendation", request, e);
            throw e;
        }
    }

    private HttpEntity<Object> createJsonEntity(Object request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(request, headers);
    }

    private void logAiRequest(String url, Object request) {
        try {
            log.info("AI server request url={}, body={}", url, objectMapper.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize AI server request body", e);
        }
    }

    private void logAiError(String name, Object request, RestClientResponseException e) {
        try {
            log.error(
                    "AI server {} failed. status={}, responseBody={}, requestBody={}",
                    name,
                    e.getStatusCode(),
                    e.getResponseBodyAsString(),
                    objectMapper.writeValueAsString(request)
            );
        } catch (JsonProcessingException jsonException) {
            log.error(
                    "AI server {} failed. status={}, responseBody={}",
                    name,
                    e.getStatusCode(),
                    e.getResponseBodyAsString()
            );
        }
    }

    public byte[] requestFaceMosaic(MultipartFile image) {
        String url = aiServerUrl + "/api/v1/images/face-mosaic-local";

        try {
            ByteArrayResource imageResource = new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename();
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", imageResource);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<byte[]> response = restTemplate.postForEntity(
                    url,
                    request,
                    byte[].class
            );

            if (response.getBody() == null || response.getBody().length == 0) {
                throw new IllegalStateException(
                        "AI 서버에서 모자이크 이미지를 반환하지 않았습니다."
                );
            }

            return response.getBody();

        } catch (IOException e) {
            throw new IllegalStateException("이미지 파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }
}
