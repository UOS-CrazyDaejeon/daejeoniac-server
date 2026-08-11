package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.client.RegionalVisitorApiClient;
import com.daejeongwang.uoscrazydaejeon.dto.response.RegionalVisitorCountResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.api.RegionalVisitorItemResponse;
import com.daejeongwang.uoscrazydaejeon.entity.RegionalVisitorCount;
import com.daejeongwang.uoscrazydaejeon.repository.RegionalVisitorCountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RegionalVisitorCountService {
    private final RegionalVisitorCountRepository regionalVisitorCountRepository;
    private final RegionalVisitorApiClient regionalVisitorApiClient;

    //전체 데이터 동기화
    @Transactional
    public void syncAllVisitorCounts(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작일은 종료일보다 늦을 수 없습니다.");
        }
        List<RegionalVisitorItemResponse> items = regionalVisitorApiClient.fetchAllVisitorCount(startDate, endDate);
        groupByDateAndSigngu(items);
    }

    //최신 데이터 동기화
    @Transactional
    public void syncLatestVisitorCounts() {
        LocalDate latestDate = regionalVisitorCountRepository.findTopByOrderByDateDesc()
                .map(RegionalVisitorCount::getDate)
                .orElseThrow(() -> new IllegalStateException("저장된 지역 방문자 수 데이터가 없습니다."));

        LocalDate startDate = latestDate.minusDays(3);
        //실제 데이터가 약 30일 지연되어 제공되므로 여유범위까지 조회
        LocalDate endDate = LocalDate.now().minusDays(20);
        if (startDate.isAfter(endDate)) {
            return;
        }

        List<RegionalVisitorItemResponse> items =  regionalVisitorApiClient.fetchAllVisitorCount(startDate, endDate);
        groupByDateAndSigngu(items);
    }

    private void saveVisitorCount(List<RegionalVisitorItemResponse> items) {
        RegionalVisitorItemResponse first = items.get(0);

        LocalDate date = LocalDate.parse(first.getBaseYmd(), DateTimeFormatter.BASIC_ISO_DATE);

        BigDecimal localVisitorCount = getVisitorCount(items, "1");
        BigDecimal outsiderVisitorCount = getVisitorCount(items, "2");
        BigDecimal foreignVisitorCount = getVisitorCount(items, "3");

        RegionalVisitorCount existing = regionalVisitorCountRepository.findByDateAndSignguCode(date, first.getSignguCode()).orElse(null);
        if(existing != null) {
            existing.updateVisitorCounts(localVisitorCount, outsiderVisitorCount, foreignVisitorCount);
            return;
        }

        RegionalVisitorCount visitorCount = RegionalVisitorCount.builder()
                .date(date)
                .signguCode(first.getSignguCode())
                .signguName(first.getSignguNm())
                .localVisitorCount(localVisitorCount)
                .outsiderVisitorCount(outsiderVisitorCount)
                .foreignVisitorCount(foreignVisitorCount)
                .build();
        regionalVisitorCountRepository.save(visitorCount);
    }

    private void groupByDateAndSigngu(List<RegionalVisitorItemResponse> items) {
        if (items.isEmpty()) {return;}
        items.stream().collect(Collectors.groupingBy(
                item -> item.getBaseYmd() + "-" + item.getSignguCode()
        )).values().forEach(this::saveVisitorCount);
    }

    private BigDecimal getVisitorCount(List<RegionalVisitorItemResponse> items, String touDivCd) {
        return items.stream()
                .filter(item -> touDivCd.equals(item.getTouDivCd()))
                .map(RegionalVisitorItemResponse::getTouNum)
                .findFirst()
                .orElse(null);
    }

    public RegionalVisitorCountResponse getVisitorCountsByDate(LocalDate date) {

        List<RegionalVisitorCountResponse.RegionalVisitorCountItem> visitorCounts = regionalVisitorCountRepository
                .findAllByDateOrderBySignguCodeAsc(date)
                .stream()
                .map(visitorCount -> RegionalVisitorCountResponse.RegionalVisitorCountItem.builder()
                        .signguCode(visitorCount.getSignguCode())
                        .signguName(visitorCount.getSignguName())
                        .localVisitorCount(visitorCount.getLocalVisitorCount())
                        .outsiderVisitorCount(visitorCount.getOutsiderVisitorCount())
                        .foreignVisitorCount(visitorCount.getForeignVisitorCount())
                        .build()
                )
                .toList();

        return RegionalVisitorCountResponse.builder()
                .date(date)
                .regionalVisitorCounts(visitorCounts)
                .build();
    }
}
