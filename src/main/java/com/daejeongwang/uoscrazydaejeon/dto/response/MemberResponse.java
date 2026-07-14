package com.daejeongwang.uoscrazydaejeon.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponse {

    private Long memberId;

    private String memberName;

    private String phone;

    private Integer point;

    private Integer coupon;

}
