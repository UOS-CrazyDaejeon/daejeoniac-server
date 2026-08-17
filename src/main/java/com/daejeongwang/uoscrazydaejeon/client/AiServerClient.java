package com.daejeongwang.uoscrazydaejeon.client;

import com.daejeongwang.uoscrazydaejeon.dto.request.ReceiptOcrRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.UUID;

@Component
public class AiServerClient {
    private final RestTemplate restTemplate;

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

    public void requestOcr(UUID receiptUuid, String objectKey) {

        ReceiptOcrRequest request =
                new ReceiptOcrRequest(receiptUuid, objectKey);

        restTemplate.postForEntity(
                aiServerUrl + "/ocr",
                request,
                Void.class
        );
    }
}
