package com.daejeongwang.uoscrazydaejeon.controller.user;


import com.daejeongwang.uoscrazydaejeon.dto.response.VisitedPlaceListResponse;
import com.daejeongwang.uoscrazydaejeon.service.VisitedPlaceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/members/me/visited-places")
@RequiredArgsConstructor
@Tag(name = "Visited Place", description = "방문 장소 API")
public class VisitedPlaceController {
    private final VisitedPlaceService visitedPlaceService;

    @GetMapping
    public ResponseEntity<List<VisitedPlaceListResponse>> getMyVisitedPlaces() {
        List<VisitedPlaceListResponse> response = visitedPlaceService.getMyVisitedPlaces();

        return ResponseEntity.ok(response);
    }
}
