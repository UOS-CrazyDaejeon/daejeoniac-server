package com.daejeongwang.uoscrazydaejeon.controller;

import com.daejeongwang.uoscrazydaejeon.service.CongestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/sync")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "관리자 API")
public class CongestionSyncController {

    private final CongestionService congestionService;

    @PostMapping("/congestion")
    @Operation(summary = "장소 예상 혼잡도 저장", description = "각 장소에 대한 날짜 별 예상 혼잡도를 DB에 저장합니다.")
    public ResponseEntity<String> syncAllPlaces() {
        congestionService.syncCongestions();

        return ResponseEntity.ok("각 장소 혼잡도 동기화 완료");
    }
}
