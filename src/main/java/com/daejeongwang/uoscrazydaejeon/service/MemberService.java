package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.dto.request.MemberUpdateRequest;
import com.daejeongwang.uoscrazydaejeon.dto.response.MemberResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.PointResponse;
import com.daejeongwang.uoscrazydaejeon.entity.AppleRefreshToken;
import com.daejeongwang.uoscrazydaejeon.entity.Member;
import com.daejeongwang.uoscrazydaejeon.exception.ResourceNotFoundException;
import com.daejeongwang.uoscrazydaejeon.repository.AppleRefreshTokenRepository;
import com.daejeongwang.uoscrazydaejeon.repository.MemberRepository;
import com.daejeongwang.uoscrazydaejeon.repository.PlaceClickLogRepository;
import com.daejeongwang.uoscrazydaejeon.repository.PlacePhotoRepository;
import com.daejeongwang.uoscrazydaejeon.repository.ReceiptRepository;
import com.daejeongwang.uoscrazydaejeon.repository.RefreshTokenRepository;
import com.daejeongwang.uoscrazydaejeon.repository.RewardDrawLogRepository;
import com.daejeongwang.uoscrazydaejeon.repository.VisitedPlaceRepository;
import com.daejeongwang.uoscrazydaejeon.util.AppleUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RewardDrawLogRepository rewardDrawLogRepository;
    private final ReceiptRepository receiptRepository;
    private final VisitedPlaceRepository visitedPlaceRepository;
    private final PlacePhotoRepository placePhotoRepository;
    private final PlaceClickLogRepository placeClickLogRepository;
    private final AppleRefreshTokenRepository appleRefreshTokenRepository;
    private final AppleUtil appleUtil;
    private final S3Service s3Service;

    public MemberResponse findMemberById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));

        return MemberResponse.builder()
                .memberId(member.getId())
                .memberName(member.getMemberName())
                .nickname(member.getNickname())
                .point(member.getPoint())
                .locationTermsAgreed(member.isLocationTermsAgreed())
                .build();
    }

    public PointResponse getMemberPoint(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));

        return PointResponse.builder()
                .memberName(member.getMemberName())
                .nickname(member.getNickname())
                .point(member.getPoint())
                .build();
    }

    @Transactional
    public MemberResponse updateMember(Long memberId, MemberUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));

        memberRepository.findByNickname(request.nickname())
                .filter(existingMember -> !existingMember.getId().equals(memberId))
                .ifPresent(existingMember -> {
                    throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
                });

        member.updateProfile(request.nickname());

        return MemberResponse.builder()
                .memberId(member.getId())
                .memberName(member.getMemberName())
                .nickname(member.getNickname())
                .point(member.getPoint())
                .locationTermsAgreed(member.isLocationTermsAgreed())
                .build();
    }

    @Transactional
    public MemberResponse agreeToLocationTerms(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));

        member.agreeToLocationTerms();

        return MemberResponse.builder()
                .memberId(member.getId())
                .memberName(member.getMemberName())
                .nickname(member.getNickname())
                .point(member.getPoint())
                .locationTermsAgreed(member.isLocationTermsAgreed())
                .build();
    }

    @Transactional
    public MemberResponse withdrawLocationTermsAgreement(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));

        member.withdrawLocationTermsAgreement();

        return MemberResponse.builder()
                .memberId(member.getId())
                .memberName(member.getMemberName())
                .nickname(member.getNickname())
                .point(member.getPoint())
                .locationTermsAgreed(member.isLocationTermsAgreed())
                .build();
    }

    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));

        appleRefreshTokenRepository.findByUserId(memberId)
                .map(AppleRefreshToken::getToken)
                .ifPresent(appleUtil::revokeAppleToken);

        placePhotoRepository.findObjectKeysByMemberId(memberId).forEach(s3Service::deleteObject);
        receiptRepository.findObjectKeysByMemberId(memberId).forEach(s3Service::deleteObject);

        appleRefreshTokenRepository.deleteByUserId(memberId);
        refreshTokenRepository.deleteByUserId(memberId);
        rewardDrawLogRepository.deleteAllByMember_Id(memberId);
        receiptRepository.deleteAllByVisitedPlace_Member_Id(memberId);
        visitedPlaceRepository.deleteAllByMember_Id(memberId);
        placePhotoRepository.deleteAllByMember_Id(memberId);
        placeClickLogRepository.detachMember(memberId);
        memberRepository.delete(member);
    }
}
