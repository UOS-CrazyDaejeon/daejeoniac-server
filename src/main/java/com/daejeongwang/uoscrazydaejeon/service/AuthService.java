package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.dto.request.LoginRequest;
import com.daejeongwang.uoscrazydaejeon.dto.request.SignUpRequest;
import com.daejeongwang.uoscrazydaejeon.dto.response.AppleResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.KakaoResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.LoginResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.SignUpResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.TokenResponse;
import com.daejeongwang.uoscrazydaejeon.entity.Member;
import com.daejeongwang.uoscrazydaejeon.entity.Refresh;
import com.daejeongwang.uoscrazydaejeon.exception.AuthenticationFailedException;
import com.daejeongwang.uoscrazydaejeon.repository.MemberRepository;
import com.daejeongwang.uoscrazydaejeon.repository.RefreshTokenRepository;
import com.daejeongwang.uoscrazydaejeon.security.JwtProvider;
import com.daejeongwang.uoscrazydaejeon.util.AppleUtil;
import com.daejeongwang.uoscrazydaejeon.util.KakaoUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final KakaoUtil kakaoUtil;
    private final AppleUtil appleUtil;

    // 관리자 회원 가입
    @Transactional
    public SignUpResponse signup(SignUpRequest request) {
        if(!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        if(memberRepository.existsByLoginId(request.getLoginId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Member member = Member.create(
                request.getLoginId(),
                Member.Role.ADMIN,
                encodedPassword,
                request.getMembername(),
                request.getNickname(),
                request.getNickname()
        );

        Member savedMember = memberRepository.save(member);

        return new SignUpResponse(
                savedMember.getId(),
                savedMember.getLoginId(),
                savedMember.getMemberName(),
                savedMember.getRole()
        );
    }

    // 관리자 로그인
    @Transactional
    public LoginResponse login(LoginRequest request) {

        String loginId = request.getLoginId();

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new AuthenticationFailedException("존재하지 않는 사용자입니다."));

        if(!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return issueLoginToken(member);
    }

    private LoginResponse issueLoginToken(Member member) {

        String accessToken = jwtProvider.generateAccessToken(member);
        String refreshToken = jwtProvider.generateRefreshToken(member);

        Refresh savedToken = refreshTokenRepository.findByUserId(member.getId())
                .orElse(null);

        if(savedToken == null) {
            refreshTokenRepository.save(
                    Refresh.builder()
                            .userId(member.getId())
                            .token(refreshToken)
                            .expiresAt(LocalDateTime.now().plusDays(7))
                            .build()
            );
        } else {
            savedToken.updateToken(
                    refreshToken,
                    LocalDateTime.now().plusDays(7)
            );
        }

        return new LoginResponse(member.getId(), member.getMemberName(), member.getRole(), accessToken, refreshToken, "Bearer");
    }

    // 토큰 갱신
    @Transactional
    public TokenResponse reissue(String refreshToken) {

        if(!jwtProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 refresh token입니다.");
        }

        if(!"refresh".equals(jwtProvider.getTokenType(refreshToken))) {
            throw new IllegalArgumentException("refresh token이 아닙니다.");
        }

        Refresh savedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("저장된 refresh token이 아닙니다."));

        if(savedToken.isExpired()) {
            refreshTokenRepository.delete(savedToken);
            throw new IllegalArgumentException("만료된 refresh token입니다.");
        }

        Long userId = jwtProvider.getUserIdAsLong(refreshToken);

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String newAccessToken = jwtProvider.generateAccessToken(member);
        String newRefreshToken = jwtProvider.generateRefreshToken(member);

        savedToken.updateToken(
                newRefreshToken,
                LocalDateTime.now().plusDays(14)
        );

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    // 카카오 로그인
    @Transactional
    public LoginResponse kakaoLogin(String kakaoAuthorizationCode) {

        KakaoResponse.OAuthToken kakaoOAuthToken = kakaoUtil.requestKakaoToken(kakaoAuthorizationCode);
        String kakaoAccessToken = kakaoOAuthToken.getAccess_token();
        KakaoResponse.KakaoProfile kakaoProfile = kakaoUtil.requestKakaoProfile(kakaoAccessToken);

        String kakaoLoginId = "kakao_" + kakaoProfile.getId();
        String kakaoNickname = kakaoProfile.getKakao_account().getProfile().getNickname();

        Member member = memberRepository.findByLoginId(kakaoLoginId)
                .orElseGet(() -> {
                    Member newMember = Member.builder()
                            .loginId(kakaoLoginId)
                            .password(passwordEncoder.encode("KAKAO_USER"))
                            .role(Member.Role.USER)
                            .memberName(kakaoNickname)
                            .build();

                    return memberRepository.save(newMember);
                });

        return issueLoginToken(member);
    }

    // 애플 로그인
    @Transactional
    public LoginResponse appleLogin(String appleAuthorizationCode) {

        AppleResponse.OAuthToken appleOAuthToken = appleUtil.requestAppleToken(appleAuthorizationCode);
        AppleResponse.AppleProfile appleProfile = appleUtil.parseAppleProfile(appleOAuthToken.getId_token());

        String appleLoginId = "apple_" + appleProfile.subject();
        String appleMemberName = appleProfile.email() == null ? "Apple User" : appleProfile.email();

        Member member = memberRepository.findByLoginId(appleLoginId)
                .orElseGet(() -> {
                    Member newMember = Member.builder()
                            .loginId(appleLoginId)
                            .password(passwordEncoder.encode("APPLE_USER"))
                            .role(Member.Role.USER)
                            .memberName(appleMemberName)
                            .point(0)
                            .createdAt(LocalDateTime.now())
                            .build();

                    return memberRepository.save(newMember);
                });

        return issueLoginToken(member);
    }

}
