package com.daejeongwang.uoscrazydaejeon.client;

import tools.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class OpenAiClient {

    @Value("${open-api.service-key}")
    private String openApiKey;

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
                .header("Authorization", "Bearer " + openApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(this::extractOutputText)
                .block();
    }

    private String extractOutputText(JsonNode response) {
        for(JsonNode output : response.path("output")) {
            for(JsonNode content : output.path("content")) {
                if("output_text".equals(content.path("type").asText())) {
                    return content.path("text").asText();
                }
            }
        }

        throw new IllegalStateException(
                "OpenAI 응답에서 텍스트를 찾지 못했습니다: " + response
        );
    }
}