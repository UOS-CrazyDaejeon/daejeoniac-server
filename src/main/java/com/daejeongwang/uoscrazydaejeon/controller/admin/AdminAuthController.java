package com.daejeongwang.uoscrazydaejeon.controller.admin;

import com.daejeongwang.uoscrazydaejeon.config.SwaggerExamples;
import com.daejeongwang.uoscrazydaejeon.dto.ResultDto;
import com.daejeongwang.uoscrazydaejeon.dto.request.LoginRequest;
import com.daejeongwang.uoscrazydaejeon.dto.request.SignUpRequest;
import com.daejeongwang.uoscrazydaejeon.dto.response.LoginResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.SignUpResponse;
import com.daejeongwang.uoscrazydaejeon.service.AuthService;
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "관리자 회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 관리자 회원가입 요청",
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
    public ResponseEntity<SignUpResponse> signup(@RequestBody @Valid SignUpRequest request) {
        SignUpResponse response = authService.signup(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "관리자 전용 로그인", description = "관리자 전용 계정으로 로그인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "관리자 로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 관리자 로그인 요청",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.BAD_REQUEST)
                    )),
            @ApiResponse(responseCode = "401", description = "관리자 인증 실패",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class)
                    )),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }
}
