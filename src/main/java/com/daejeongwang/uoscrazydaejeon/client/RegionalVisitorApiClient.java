package com.daejeongwang.uoscrazydaejeon.client;

import com.daejeongwang.uoscrazydaejeon.dto.response.api.RegionalVisitorApiResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.api.RegionalVisitorItemResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class RegionalVisitorApiClient {
    private final List<String> signguCodes = List.of(
            "30110", // 동구
            "30140", // 중구
            "30170", // 서구
            "30200", // 유성구
            "30230"  // 대덕구
    );

    @Value("${daejeon.api.service-key}")
    private String serviceKey;
    private final String visitorApiUrl = "https://apis.data.go.kr/B551011/DataLabService/locgoRegnVisitrDDList";

    public List<RegionalVisitorItemResponse> fetchAllVisitorCount(LocalDate startDate, LocalDate endDate) {
        RestTemplate restTemplate = new RestTemplate();
        List<RegionalVisitorItemResponse> result = new ArrayList<>();

        String startYmd = startDate.format(DateTimeFormatter.BASIC_ISO_DATE);
        String endYmd = endDate.format(DateTimeFormatter.BASIC_ISO_DATE);

        int pageNo = 1;
        int numOfRows = 1000;

        while (true) {

            String url = UriComponentsBuilder.fromUriString(visitorApiUrl)
                    .queryParam("serviceKey", serviceKey)
                    .queryParam("pageNo", pageNo)
                    .queryParam("numOfRows", numOfRows)
                    .queryParam("MobileOS", "ETC")
                    .queryParam("MobileApp", "UosCrazyDaejeon")
                    .queryParam("_type", "json")
                    .queryParam("startYmd", startYmd)
                    .queryParam("endYmd", endYmd)
                    .toUriString();

            RegionalVisitorApiResponse response = restTemplate.getForObject(url, RegionalVisitorApiResponse.class);

            if (response == null
                    || response.getResponse() == null
                    || response.getResponse().getBody() == null) {
                throw new IllegalStateException("지역 방문자 수 API 응답이 없습니다.");
            }

            if (response.getResponse().getHeader() != null
                    && !"0000".equals(response.getResponse().getHeader().getResultCode())) {
                throw new IllegalStateException("지역 방문자 수 API 호출 실패: " + response.getResponse().getHeader().getResultMsg());
            }

            RegionalVisitorApiResponse.Body body = response.getResponse().getBody();

            if (body.getItems() != null && body.getItems().getItem() != null) {
                List<RegionalVisitorItemResponse> daejeonItems =
                        body.getItems().getItem().stream()
                                .filter(item -> signguCodes.contains(item.getSignguCode()))
                                .toList();
                result.addAll(daejeonItems);
            }

            int totalCount = body.getTotalCount() == null
                    ? 0
                    : body.getTotalCount();

            if (pageNo * numOfRows >= totalCount) {
                break;
            }

            pageNo++;
        }
        return result;
    }
}
