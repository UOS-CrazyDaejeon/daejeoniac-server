package com.daejeongwang.uoscrazydaejeon.controller;

import com.daejeongwang.uoscrazydaejeon.service.PlaceClickLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
@Tag(name = "ClickLog", description = "장소 클릭 횟수 조회 API")
public class PlaceClickLogController {

    private final PlaceClickLogService placeClickLogService;

    @PostMapping("/{placeId}/clicks")
    @Operation(summary = "장소 별 클릭 로그 API", description = "장소가 클릭된 횟수를 증가시킵니다.")
    public ResponseEntity<Void> saveClickLog(
            @PathVariable Long placeId
    ) {
        placeClickLogService.saveClickLog(placeId);
        return ResponseEntity.ok().build();
    }
}