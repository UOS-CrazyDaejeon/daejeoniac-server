package com.daejeongwang.uoscrazydaejeon.controller.user;

import com.daejeongwang.uoscrazydaejeon.config.SwaggerExamples;
import com.daejeongwang.uoscrazydaejeon.dto.ResultDto;
import com.daejeongwang.uoscrazydaejeon.dto.request.MemberUpdateRequest;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
@Tag(name = "Member", description = "회원 정보 API")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "현재 로그인 된 사용자의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공", useReturnTypeSchema = true),
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
    public ResponseEntity<MemberResponse> getMemberInfo(Authentication authentication) {
        Long memberId = Long.valueOf(authentication.getName());

        MemberResponse response = memberService.findMemberById(memberId);

        return ResponseEntity.ok(response);
    }

     @GetMapping("/me/points")
    @Operation(summary = "내 포인트 조회", description = "현재 로그인 된 사용자의 포인트를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 포인트 조회 성공", useReturnTypeSchema = true),
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
    public ResponseEntity<PointResponse> getMemberPoints(Authentication authentication) {
        Long memberId = Long.valueOf(authentication.getName());

        PointResponse response = memberService.getMemberPoint(memberId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me")
    @Operation(summary = "내 정보 수정", description = "현재 로그인 된 사용자의 닉네임을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 중복 닉네임",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.BAD_REQUEST)
                    )),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.NOT_FOUND)
                    )),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<MemberResponse> updateMember(
            Authentication authentication,
            @Valid @RequestBody MemberUpdateRequest request
    ) {
        Long memberId = Long.valueOf(authentication.getName());

        MemberResponse response = memberService.updateMember(memberId, request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/me/location-terms/agree")
    @Operation(summary = "위치기반서비스 이용약관 동의", description = "현재 로그인 된 사용자의 위치기반서비스 이용약관 동의 상태를 true로 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "위치기반서비스 이용약관 동의 처리 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.UNAUTHORIZED)
                    )),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.NOT_FOUND)
                    )),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<MemberResponse> agreeToLocationTerms(Authentication authentication) {
        Long memberId = Long.valueOf(authentication.getName());

        MemberResponse response = memberService.agreeToLocationTerms(memberId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/me/location-terms/agree")
    @Operation(summary = "위치기반서비스 이용약관 동의 철회", description = "현재 로그인 된 사용자의 위치기반서비스 이용약관 동의 상태를 false로 변경합니다. 회원 계정은 삭제되지 않습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "위치기반서비스 이용약관 동의 철회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.UNAUTHORIZED)
                    )),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.NOT_FOUND)
                    )),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<MemberResponse> withdrawLocationTermsAgreement(Authentication authentication) {
        Long memberId = Long.valueOf(authentication.getName());

        MemberResponse response = memberService.withdrawLocationTermsAgreement(memberId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/me")
    @Operation(summary = "회원 탈퇴", description = "현재 로그인 된 사용자의 계정과 관련 데이터를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "회원 탈퇴 성공", content = @Content),
            @ApiResponse(responseCode = "400", description = "잘못된 회원 ID",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.BAD_REQUEST)
                    )),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.NOT_FOUND)
                    )),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<Void> deleteMember(Authentication authentication) {
        Long memberId = Long.valueOf(authentication.getName());

        memberService.deleteMember(memberId);

        return ResponseEntity.noContent().build();
    }
}
