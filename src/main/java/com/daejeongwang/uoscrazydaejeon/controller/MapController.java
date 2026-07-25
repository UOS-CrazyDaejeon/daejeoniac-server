package com.daejeongwang.uoscrazydaejeon.controller;

import com.daejeongwang.uoscrazydaejeon.dto.response.PlaceSearchResponse;
import com.daejeongwang.uoscrazydaejeon.service.PlaceSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
@Tag(name = "Place Search", description = "장소 검색 API")
public class MapController {

    private final PlaceSearchService placeSearchService;

    @GetMapping("/search")
    @Operation(summary = "장소 검색", description = "실시간 호출하여 장소 검색 결과를 반환")
    public ResponseEntity<List<PlaceSearchResponse>> searchPlaces(@RequestParam String keyword) {
        List<PlaceSearchResponse> result = placeSearchService.searchPlaces(keyword);

        return ResponseEntity.ok(result);
    }
}
