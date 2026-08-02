package com.daejeongwang.uoscrazydaejeon.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {

    @NotEmpty(message = "사용자 ID는 필수 항목입니다.")
    private String loginId;

    @NotEmpty(message = "사용자 이름은 필수 항목입니다.")
    private String membername;

    @NotEmpty(message = "닉네임은 필수 항목입니다.")
    private String nickname;

    @NotEmpty(message = "전화번호는 필수 항목입니다.")
    private String phone;

    @NotEmpty(message = "비밀번호는 필수 항목입니다.")
    private String password;

    @NotEmpty(message = "비밀번호 확인은 필수 항목입니다.")
    private String confirmPassword;

}
