package com.daejeongwang.uoscrazydaejeon.controller.admin;

import com.daejeongwang.uoscrazydaejeon.dto.request.LoginRequest;
import com.daejeongwang.uoscrazydaejeon.dto.request.SignUpRequest;
import com.daejeongwang.uoscrazydaejeon.dto.response.LoginResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.SignUpResponse;
import com.daejeongwang.uoscrazydaejeon.entity.Member;
import com.daejeongwang.uoscrazydaejeon.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/auth")
@RequiredArgsConstructor
@Tag(name = "Admin-Auth", description = "관리자 전용 회원가입/로그인 API")
public class AdminAuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "관리자 전용 회원가입", description = "관리자 전용 계정을 생성합니다.")
    public ResponseEntity<SignUpResponse> signup(@RequestBody @Valid SignUpRequest request) {
        SignUpResponse response = authService.signup(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "관리자 전용 로그인", description = "관리자 전용 계정으로 로그인합니다.")
    public ResponseEntity<LoginResponse> signup(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }
}
