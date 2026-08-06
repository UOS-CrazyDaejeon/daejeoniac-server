package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.client.CongestionApiClient;
import com.daejeongwang.uoscrazydaejeon.client.OpenAiClient;
import com.daejeongwang.uoscrazydaejeon.dto.response.api.CongestionItemResponse;
import com.daejeongwang.uoscrazydaejeon.entity.Congestion;
import com.daejeongwang.uoscrazydaejeon.entity.Place;
import com.daejeongwang.uoscrazydaejeon.repository.CongestionRepository;
import com.daejeongwang.uoscrazydaejeon.repository.PlaceRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class CongestionService {

    private final CongestionApiClient congestionApiClient;
    private final CongestionRepository congestionRepository;
    private final PlaceRepository placeRepository;
    private final OpenAiClient openAiClient;

    @Transactional
    public void syncCongestions() {
        List<CongestionItemResponse> response = congestionApiClient.fetchAllCongestions();

        for(CongestionItemResponse congestionItemResponse : response) {
            Congestion congestion = convertCongestionToEntity(congestionItemResponse);

            if(congestion == null)
                continue;

            if(!congestionRepository.existsByPlaceAndDate(congestion.getPlace(), congestion.getDate())) {
                congestionRepository.save(congestion);
            }
        }
    }

    public Congestion convertCongestionToEntity(CongestionItemResponse dto) {
        Place place = placeRepository.findByPlaceName(dto.getTAtsNm()).orElse(null);

        if(place == null)
            return null;

        return Congestion.builder()
                .place(place)
                .placeName(dto.getTAtsNm())
                .gu(dto.getSignguNm())
                .date(dto.getBaseYmd())
                .congestionRate(Double.valueOf(dto.getCnctrRate()))
                .build();
    }


    private Double parseCongestionRate(String value) {
        if(value == null || value.isBlank())
            return null;

        return Double.valueOf(value);
    }

    @Transactional
    public void generateCongestions() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE;

        for(Place place : placeRepository.findAll()) {
            // 현재는 1개만 저장(오늘 날짜)
            for(int offset = 0; offset < 1; offset++) {
                LocalDate targetDate = today.plusDays(offset);
                String date = targetDate.format(formatter);

                if(congestionRepository.existsByPlaceAndDate(place, date)) {
                    continue;
                }

                Double rate = estimateCongestionRate(place, targetDate);
                if(rate == null) {
                    continue;
                }

                congestionRepository.save(Congestion.builder()
                        .place(place)
                        .placeName(place.getPlaceName())
                        .gu(place.getGu())
                        .date(date)
                        .congestionRate(rate)
                        .build());
            }
        }
    }

    private Double estimateCongestionRate(Place place, LocalDate targetDate) {
        String prompt = createCongestionPrompt(place, targetDate);

        try {
            double rate = Double.parseDouble(
                    openAiClient.generateCongestion(prompt).trim()
            );

            return Math.max(0.0, Math.min(100.0, rate));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String createCongestionPrompt(Place place, LocalDate targetDate) {
        String date = targetDate.format(
                DateTimeFormatter.ofPattern("yyyy년 MM월 dd일", Locale.KOREAN)
        );

        String dayOfWeek = targetDate.getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.KOREAN);

        return """
            너는 대전 지역 장소의 데모용 혼잡도 예측 도우미다.
            아래 장소의 지정 날짜 예상 혼잡도를 0부터 100 사이의 정수 하나로만 출력해.

            예측 대상 날짜: %s
            요일: %s

            장소명: %s
            지역: %s
            카테고리: %s

            판단 기준:
            - 요일 특성(평일, 주말)을 반영한다.
            - 카테고리와 장소 특성에 따른 일반적인 방문 패턴을 반영한다.
            - 실제 실시간 혼잡도나 사실을 주장하지 말고 데모용 추정치로만 판단한다.
            - 설명, 단위, JSON, 문장은 절대 출력하지 않는다.
            - 반드시 0부터 100 사이의 정수 하나만 출력한다.
            """.formatted(
                date,
                dayOfWeek,
                place.getPlaceName(),
                place.getGu(),
                place.getCategoryLarge()
        );
    }
}
