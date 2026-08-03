package com.daejeongwang.uoscrazydaejeon.dto.response;

import com.daejeongwang.uoscrazydaejeon.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private long id;

    private String memberName;

    private Member.Role role;

    private String accessToken;

    private String refreshToken;

    private String TokenType;

}
