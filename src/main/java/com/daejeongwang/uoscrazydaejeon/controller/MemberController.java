package com.daejeongwang.uoscrazydaejeon.controller;

import com.daejeongwang.uoscrazydaejeon.config.SwaggerExamples;
import com.daejeongwang.uoscrazydaejeon.dto.ResultDto;
import com.daejeongwang.uoscrazydaejeon.dto.response.MemberResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.PointResponse;
import com.daejeongwang.uoscrazydaejeon.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
@Tag(name = "Member", description = "회원 정보 API")
public class MemberController {

    private final MemberService memberService;

    // TODO : 로그인 기능 구현 뒤 GET member/me API로 변경 예정
    @GetMapping("/{memberId}")
    @Operation(summary = "내 정보 조회", description = "현재 로그인 된 사용자의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 회원 ID",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.BAD_REQUEST)
                    )),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<MemberResponse> getMemberInfo(@PathVariable Long memberId) {
        MemberResponse response = memberService.findMemberById(memberId);

        return ResponseEntity.ok(response);
    }

    // TODO : 파라미터를 member로 받지 말고 현재 로그인 된 사용자의 포인트 조회 기능으로 수정 필요
    // @GetMapping("/me/points")
    @GetMapping("/{memberId}/points")
    @Operation(summary = "내 포인트 조회", description = "현재 로그인 된 사용자의 포인트를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 포인트 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 회원 ID",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.BAD_REQUEST)
                    )),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<PointResponse> getMemberPoints(@PathVariable Long memberId) {
        PointResponse response = memberService.getMemberPoint(memberId);

        return ResponseEntity.ok(response);
    }
}
