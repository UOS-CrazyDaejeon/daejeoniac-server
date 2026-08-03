package com.daejeongwang.uoscrazydaejeon.controller.user;

import com.daejeongwang.uoscrazydaejeon.config.SwaggerExamples;
import com.daejeongwang.uoscrazydaejeon.dto.ResultDto;
import com.daejeongwang.uoscrazydaejeon.dto.response.RewardDrawLogResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.RewardDrawResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.RewardItemResponse;
import com.daejeongwang.uoscrazydaejeon.service.RewardDrawService;
import com.daejeongwang.uoscrazydaejeon.service.RewardItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reward")
@RequiredArgsConstructor
@Tag(name = "Reward", description = "상품 정보 API")
public class RewardController {

    private final RewardItemService rewardItemService;
    private final RewardDrawService rewardDrawService;

    // 전체 상품 조회
    @GetMapping
    @Operation(summary = "상품 목록 조회", description = "등록되어있는 상품 전체 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 목록 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<List<RewardItemResponse>> findAll() {
        List<RewardItemResponse> responses = rewardItemService.findAll()
                .stream()
                .map(RewardItemResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/draw")
    @Operation(summary = "승인된 영수증에 대한 상품 뽑기", description = "승인된 영수증에 대해 랜덤한 상품을 뽑습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 뽑기 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 상품 뽑기 요청",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.BAD_REQUEST)
                    )),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class)
                    )),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<RewardDrawResponse> drawReward(Authentication authentication, @RequestParam Long visitedPlaceId) {
        Long memberId = Long.valueOf(authentication.getName());

        RewardDrawResponse response = rewardDrawService.drawReward(memberId, visitedPlaceId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/draw-logs")
    @Operation(summary = "뽑기 기록 조회", description = "나의 뽑기 기록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "뽑기 기록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class)
                    )),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<List<RewardDrawLogResponse>> getMyDrawLogs(Authentication authentication) {
        Long memberId = Long.valueOf(authentication.getName());

        List<RewardDrawLogResponse> response = rewardDrawService.findMyRewardDrawLogs(memberId);

        return ResponseEntity.ok(response);
    }

}
