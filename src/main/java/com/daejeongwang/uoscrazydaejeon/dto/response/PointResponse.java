package com.daejeongwang.uoscrazydaejeon.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PointResponse {

    @Schema(description = "회원 이름", example = "홍길동")
    private String memberName;

    @Schema(description = "닉네임", example = "대전왕")
    private String nickname;

    @Schema(description = "보유 포인트", example = "1500")
    private Integer point;

}
