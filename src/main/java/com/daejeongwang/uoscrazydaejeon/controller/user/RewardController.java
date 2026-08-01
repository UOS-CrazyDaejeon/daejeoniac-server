package com.daejeongwang.uoscrazydaejeon.controller.user;

import com.daejeongwang.uoscrazydaejeon.dto.response.RewardItemResponse;
import com.daejeongwang.uoscrazydaejeon.service.RewardItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reward")
@RequiredArgsConstructor
@Tag(name = "Reward", description = "상품 정보 API")
public class RewardController {

    private final RewardItemService rewardItemService;

    // 전체 상품 조회
    @GetMapping
    @Operation(summary = "상품 목록 조회", description = "등록되어있는 상품 전체 목록을 조회합니다.")
    public ResponseEntity<List<RewardItemResponse>> findAll() {
        List<RewardItemResponse> responses = rewardItemService.findAll()
                .stream()
                .map(RewardItemResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

}
