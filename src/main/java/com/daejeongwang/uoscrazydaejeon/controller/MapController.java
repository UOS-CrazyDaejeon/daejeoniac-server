package com.daejeongwang.uoscrazydaejeon.controller;

import com.daejeongwang.uoscrazydaejeon.dto.response.ExternalPlaceSearchResponse;
import com.daejeongwang.uoscrazydaejeon.service.ExternalPlaceSearchService;
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
@RequestMapping("/api/v1/external/places")
@RequiredArgsConstructor
@Tag(name = "External Place", description = "외부 장소 검색 API")
public class MapController {

    private final ExternalPlaceSearchService externalPlaceSearchService;

    @GetMapping("/search")
    @Operation(summary = "외부 장소 검색", description = "실시간 호출하여 장소 검색 결과를 반환")
    public ResponseEntity<List<ExternalPlaceSearchResponse>> searchExternalPlaces(@RequestParam String keyword) {
        List<ExternalPlaceSearchResponse> result = externalPlaceSearchService.searchPlaces(keyword);

        return ResponseEntity.ok(result);
    }
}