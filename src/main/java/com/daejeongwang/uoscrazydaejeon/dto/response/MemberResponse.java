package com.daejeongwang.uoscrazydaejeon.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponse {

    @Schema(description = "회원 ID", example = "1")
    private Long memberId;

    @Schema(description = "회원 이름", example = "홍길동")
    private String memberName;

    @Schema(description = "닉네임", example = "대전왕")
    private String nickname;

    @Schema(description = "보유 포인트", example = "1500")
    private Integer point;

    @Schema(description = "위치기반서비스 이용약관 동의 여부", example = "true")
    private boolean locationTermsAgreed;

    @Schema(description = "보유 쿠폰 수", example = "2")
    private Integer coupon;

}
