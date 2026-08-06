package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.client.OpenAiClient;
import com.daejeongwang.uoscrazydaejeon.dto.response.VisitorCountResponse;
import com.daejeongwang.uoscrazydaejeon.entity.Place;
import com.daejeongwang.uoscrazydaejeon.entity.VisitorCount;
import com.daejeongwang.uoscrazydaejeon.exception.ResourceNotFoundException;
import com.daejeongwang.uoscrazydaejeon.repository.PlaceRepository;
import com.daejeongwang.uoscrazydaejeon.repository.VisitorCountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
@AllArgsConstructor
public class VisitorCountService {
    private final VisitorCountRepository visitorCountRepository;
    private final PlaceRepository placeRepository;
    private final OpenAiClient openAiClient;

    public void generateVisitorCounts() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        for(Place place : placeRepository.findAll()) {
            // 현재는 최근 1일치만 저장(어제 날짜)
            for(int offset = 0; offset < 1; offset++) {
                LocalDate targetDate = yesterday.minusDays(offset);

                if(visitorCountRepository.existsByPlaceAndDate(place, targetDate)) {
                    continue;
                }

                Long visitorCount = estimateVisitorCount(place, targetDate);
                if(visitorCount == null) {
                    continue;
                }

                visitorCountRepository.save(VisitorCount.builder()
                        .place(place)
                        .placeName(place.getPlaceName())
                        .gu(place.getGu())
                        .date(targetDate)
                        .visitorCount(visitorCount)
                        .build());
            }
        }
    }

    private Long estimateVisitorCount(Place place, LocalDate targetDate) {
        String prompt = createVisitorCountPrompt(place, targetDate);

        try {
            long count = Long.parseLong(
                    openAiClient.generateVisitorCount(prompt).trim()
            );

            return Math.max(0L, count);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String createVisitorCountPrompt(Place place, LocalDate targetDate) {
        String date = targetDate.format(
                DateTimeFormatter.ofPattern("yyyy년 MM월 dd일", Locale.KOREAN)
        );

        String dayOfWeek = targetDate.getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.KOREAN);

        return """
            너는 대전 지역 장소의 데모용 방문자 수 생성 도우미다.
            아래 장소의 지정 날짜 일일 방문자 수를 0이상의 현실적인 범위 내의 정수 하나로만 출력해라.

            날짜: %s
            요일: %s

            장소명: %s
            지역: %s
            카테고리: %s

            판단 기준:
            - 평일과 주말의 방문 패턴 차이를 반영한다.
            - 장소의 인지도, 규모 및 카테고리 특성을 반영한다.
            - 실제 통계나 사실을 주장하지 말고 데모용 추정치로만 판단한다.
            - 설명, 단위, JSON, 문장은 절대 출력하지 않는다.
            - 반드시 0이상의 정수 하나만 출력한다.
            """.formatted(
                date,
                dayOfWeek,
                place.getPlaceName(),
                place.getGu(),
                place.getCategoryLarge()
        );
    }

    public VisitorCountResponse getVisitorCountByPlace(Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new ResourceNotFoundException("장소가 없습니다."));

        List<VisitorCountResponse.VisitorCountItem> visitorCounts = visitorCountRepository
                .findAllByPlace_IdOrderByDateDesc(placeId)
                .stream()
                .map(visitorCount -> VisitorCountResponse.VisitorCountItem.builder()
                        .date(visitorCount.getDate())
                        .visitorCount(visitorCount.getVisitorCount())
                        .build()
                )
                .toList();

        return VisitorCountResponse.builder()
                .placeId(place.getId())
                .placeName(place.getPlaceName())
                .visitorCounts(visitorCounts)
                .build();
    }
}
