package com.daejeongwang.uoscrazydaejeon.controller.admin;

import com.daejeongwang.uoscrazydaejeon.dto.request.RewardItemCreateRequest;
import com.daejeongwang.uoscrazydaejeon.dto.request.RewardItemUpdateRequest;
import com.daejeongwang.uoscrazydaejeon.dto.response.api.ShoppingApiResponse;
import com.daejeongwang.uoscrazydaejeon.entity.RewardItem;
import com.daejeongwang.uoscrazydaejeon.service.RewardItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // 상품 정보 수정
    @PatchMapping("/{rewardItemId}")
    @Operation(summary = "상품 정보 수정", description = "등록된 상품의 정보를 수정합니다.")
    public ResponseEntity<RewardItem> updateRewards(
            @PathVariable Long rewardItemId,
            @RequestBody RewardItemUpdateRequest request) {
        RewardItem response = rewardItemService.updateRewardItem(rewardItemId, request);

        return ResponseEntity.ok(response);
    }

}
