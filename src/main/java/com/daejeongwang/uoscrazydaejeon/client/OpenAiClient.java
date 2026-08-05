package com.daejeongwang.uoscrazydaejeon.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenAiClient {

    @Value("${openai.api-key}")
    private String openAiApiKey;

    @Value("${openai.congestion.model}")
    private String congestionModel;

    public String generateCongestion(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "model", congestionModel,
                "input", prompt
        );

        return WebClient.create("https://api.openai.com")
                .post()
                .uri("/v1/responses")
                .header("Authorization", "Bearer " + openAiApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}