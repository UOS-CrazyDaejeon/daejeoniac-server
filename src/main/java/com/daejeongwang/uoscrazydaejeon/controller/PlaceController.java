package com.daejeongwang.uoscrazydaejeon.controller;

import com.daejeongwang.uoscrazydaejeon.dto.response.PlaceResponse;
import com.daejeongwang.uoscrazydaejeon.repository.PlaceRepository;
import com.daejeongwang.uoscrazydaejeon.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
@Tag(name = "Place", description = "장소 조회 API")
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping
    @Operation(summary = "전체 장소 조회", description = "대전의 모든 장소를 조회합니다.")
    public ResponseEntity<List<PlaceResponse>> findAllPlaces(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<PlaceResponse> response = placeService.findAllPlaces(page, size);

        return ResponseEntity.ok(response);
    }

    @GetMapping("{placeId}")
    @Operation(summary = "특정 장소 조회", description = "대전의 특정 장소를 조회합니다.")
    public ResponseEntity<PlaceResponse> getPlaceByPlaceId(@PathVariable Long placeId) {
        PlaceResponse response = placeService.getPlaceById(placeId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{placeId}/nearby")
    @Operation(summary = "특정 장소의 근처 장소 조회", description = "특정 장소의 근처 장소를 조회합니다.")
    public ResponseEntity<Page<PlaceResponse>> getNearbyPlacesByPlaceId(
            @PathVariable Long placeId,
            @RequestParam(defaultValue = "1000") Double radius,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                placeService.getNearbyPlacesByPlaceId(placeId, radius, page, size)
        );
    }
}
