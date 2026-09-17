package com.daejeongwang.uoscrazydaejeon.config;

import com.daejeongwang.uoscrazydaejeon.service.CongestionService;
import com.daejeongwang.uoscrazydaejeon.service.PlaceService;
import com.daejeongwang.uoscrazydaejeon.service.RegionalVisitorCountService;
import com.daejeongwang.uoscrazydaejeon.service.VisitorCountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TourismDataScheduler {

    private final PlaceService placeService;
    private final CongestionService congestionService;
    private final RegionalVisitorCountService regionalVisitorCountService;
    private final VisitorCountService visitorCountService;

    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
    public void syncTourismDataDaily() {
        log.info("Start scheduled tourism data sync");

        placeService.syncAllPlaces();
        congestionService.syncCongestions();
        regionalVisitorCountService.syncLatestVisitorCounts();

        log.info("Finish scheduled tourism data sync");
    }

    // LLM 호출 비용으로 인해 자동 실행 중지. 필요할 때 관리자 API로 수동 실행한다.
    // @Scheduled(cron = "0 30 4 * * *", zone = "Asia/Seoul")
    public void generateTourismForecastDataDaily() {
        log.info("Start scheduled tourism forecast data generation");

        congestionService.generateCongestions();
        visitorCountService.generateVisitorCounts();

        log.info("Finish scheduled tourism forecast data generation");
    }
}
