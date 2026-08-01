package com.daejeongwang.uoscrazydaejeon.controller.admin;

import com.daejeongwang.uoscrazydaejeon.dto.request.RewardItemCreateRequest;
import com.daejeongwang.uoscrazydaejeon.entity.RewardItem;
import com.daejeongwang.uoscrazydaejeon.service.RewardItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/rewards")
@Tag(name = "Admin-Reward", description = "관리자 전용 상품 관리 API")
public class AdminRewardController {

    private final RewardItemService rewardItemService;

    // 새 상품 등록
    @PostMapping("/save")
    @Operation(summary = "새 상품 등록", description = "새로운 상품을 저장합니다.")
    public ResponseEntity<RewardItem> saveRewards(@RequestBody RewardItemCreateRequest request) {
        RewardItem response = rewardItemService.createRewardItem(request);

        return ResponseEntity.ok(response);
    }
}
