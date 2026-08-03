package com.daejeongwang.uoscrazydaejeon.controller.user;

import com.daejeongwang.uoscrazydaejeon.config.SwaggerExamples;
import com.daejeongwang.uoscrazydaejeon.dto.ResultDto;
import com.daejeongwang.uoscrazydaejeon.dto.request.ReissueRequest;
import com.daejeongwang.uoscrazydaejeon.dto.response.LoginResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.TokenResponse;
import com.daejeongwang.uoscrazydaejeon.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

    private final AuthService authService;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-url}")
    private String redirectUrl;

    @Operation(summary = "accessToken 갱신", description = "refreshToken을 사용하여 새로운 accessToken을 발급받습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "accessToken 갱신 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 토큰 갱신 요청",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.BAD_REQUEST)
                    )),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class)
                    )),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(
                            schema = @Schema(implementation = ResultDto.class),
                            examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)
                    ))
    })
    @PostMapping("/reissue")
    public TokenResponse reissue(@RequestBody ReissueRequest request) {
        return authService.reissue(request.refreshToken());
    }

    @Operation(summary = "카카오 로그인 페이지 이동", description = "카카오 OAuth 인증 페이지로 리다이렉트합니다.")
    @GetMapping("/kakao")
    public ResponseEntity<Void> redirectToKakao() {
        String encodedRedirectUri = URLEncoder.encode(redirectUrl, StandardCharsets.UTF_8);

        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize" + "?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + encodedRedirectUri;

        return ResponseEntity.status(302)
                .header("Location", kakaoAuthUrl)
                .build();
    }

    @Operation(summary = "카카오 로그인", description = "카카오 인가 코드로 로그인하고 JWT 토큰을 발급받습니다.")
    @GetMapping("/login/kakao")
    public ResponseEntity<LoginResponse> kakaoLogin(@RequestParam("code") String kakaoAuthorizationCode) {
        LoginResponse response = authService.kakaoLogin(kakaoAuthorizationCode);

        return ResponseEntity.ok(response);
    }
}
