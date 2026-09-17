package com.daejeongwang.uoscrazydaejeon.controller.admin;

import com.daejeongwang.uoscrazydaejeon.config.SwaggerExamples;
import com.daejeongwang.uoscrazydaejeon.dto.ResultDto;
import com.daejeongwang.uoscrazydaejeon.dto.request.RewardItemCreateRequest;
import com.daejeongwang.uoscrazydaejeon.dto.request.RewardItemUpdateRequest;
import com.daejeongwang.uoscrazydaejeon.dto.request.VisitRewardItemCreateRequest;
import com.daejeongwang.uoscrazydaejeon.dto.request.VisitRewardItemUpdateRequest;
import com.daejeongwang.uoscrazydaejeon.entity.RewardItem;
import com.daejeongwang.uoscrazydaejeon.entity.VisitRewardItem;
import com.daejeongwang.uoscrazydaejeon.service.RewardItemService;
import com.daejeongwang.uoscrazydaejeon.service.VisitRewardItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/rewards")
@Tag(name = "Admin-Reward", description = "관리자 전용 상품 관리 API")
public class AdminRewardController {

    private final RewardItemService rewardItemService;
    private final VisitRewardItemService visitRewardItemService;

    // 새 상품 등록
    @PostMapping("/save")
    @Operation(summary = "새 상품 등록", description = "새로운 상품을 저장합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "새 상품 등록 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "잘못된 상품 등록 요청",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.BAD_REQUEST)
                    )),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.UNAUTHORIZED)
                    )),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<RewardItem> saveRewards(@RequestBody RewardItemCreateRequest request) {
        RewardItem response = rewardItemService.createRewardItem(request);

        return ResponseEntity.ok(response);
    }

    // 상품 정보 수정
    @PatchMapping("/{rewardItemId}")
    @Operation(summary = "상품 정보 수정", description = "등록된 상품의 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 정보 수정 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "잘못된 상품 수정 요청",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.BAD_REQUEST)
                    )),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.UNAUTHORIZED)
                    )),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<RewardItem> updateRewards(
            @PathVariable Long rewardItemId,
            @RequestBody RewardItemUpdateRequest request) {
        RewardItem response = rewardItemService.updateRewardItem(rewardItemId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/visit-rewards")
    @Operation(summary = "방문 보상 목록 조회", description = "방문 인증 장소에 사용되는 보상 목록을 조회합니다.")
    public ResponseEntity<List<VisitRewardItem>> findAllVisitRewards() {
        return ResponseEntity.ok(visitRewardItemService.findAll());
    }

    @PostMapping("/visit-rewards")
    @Operation(summary = "방문 보상 등록", description = "방문 인증 장소에 사용되는 보상을 등록합니다.")
    public ResponseEntity<VisitRewardItem> saveVisitReward(
            @RequestBody VisitRewardItemCreateRequest request
    ) {
        return ResponseEntity.ok(visitRewardItemService.create(request));
    }

    @PatchMapping("/visit-rewards/{visitRewardItemId}")
    @Operation(summary = "방문 보상 수정", description = "방문 인증 장소에 사용되는 보상 정보를 수정합니다.")
    public ResponseEntity<VisitRewardItem> updateVisitReward(
            @PathVariable Long visitRewardItemId,
            @RequestBody VisitRewardItemUpdateRequest request
    ) {
        return ResponseEntity.ok(visitRewardItemService.update(visitRewardItemId, request));
    }

}
