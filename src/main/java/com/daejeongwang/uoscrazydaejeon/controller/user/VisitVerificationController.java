package com.daejeongwang.uoscrazydaejeon.controller.user;

import com.daejeongwang.uoscrazydaejeon.dto.request.VisitVerificationRequest;
import com.daejeongwang.uoscrazydaejeon.dto.response.VisitVerificationResponse;
import com.daejeongwang.uoscrazydaejeon.service.VisitVerificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
@Tag(name = "Visit Verification", description = "방문 인증 API")
public class VisitVerificationController {
    private final VisitVerificationService visitVerificationService;

    @PostMapping("/{placeId}/visit-verifications")
    public ResponseEntity<VisitVerificationResponse> verifyVisit(
            @PathVariable("placeId") Long placeId,
            @Valid @RequestBody VisitVerificationRequest request
    ) {
        VisitVerificationResponse response = visitVerificationService.verifyVisit(placeId, request);

        return ResponseEntity.ok(response);
    }
}
