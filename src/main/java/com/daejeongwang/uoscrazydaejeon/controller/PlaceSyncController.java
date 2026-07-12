package com.daejeongwang.uoscrazydaejeon.controller;

import com.daejeongwang.uoscrazydaejeon.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/sync")
@RequiredArgsConstructor
public class PlaceSyncController {

    private final PlaceService placeService;

    @PostMapping("/places")
    public ResponseEntity<String> syncAllPlaces() {
        placeService.syncAllPlaces();

        return ResponseEntity.ok("장소 데이터 동기화 완료");
    }
}
