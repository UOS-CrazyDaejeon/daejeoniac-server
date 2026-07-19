package com.daejeongwang.uoscrazydaejeon.controller;

import com.daejeongwang.uoscrazydaejeon.dto.response.MemberResponse;
import com.daejeongwang.uoscrazydaejeon.service.MemberService;
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

    // 임시 -> 로그인 기능 구현 뒤 GET member/me API로 변경 예정
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponse> getMemberInfo(@PathVariable Long memberId) {
        MemberResponse response = memberService.findMemberById(memberId);

        return ResponseEntity.ok(response);
    }
}
