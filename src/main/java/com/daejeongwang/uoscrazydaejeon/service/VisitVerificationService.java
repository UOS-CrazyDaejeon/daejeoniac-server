package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.dto.request.VisitVerificationRequest;
import com.daejeongwang.uoscrazydaejeon.dto.response.VisitVerificationResponse;
import com.daejeongwang.uoscrazydaejeon.entity.Member;
import com.daejeongwang.uoscrazydaejeon.entity.Place;
import com.daejeongwang.uoscrazydaejeon.entity.VisitedPlace;
import com.daejeongwang.uoscrazydaejeon.exception.ConflictException;
import com.daejeongwang.uoscrazydaejeon.exception.ResourceNotFoundException;
import com.daejeongwang.uoscrazydaejeon.repository.MemberRepository;
import com.daejeongwang.uoscrazydaejeon.repository.PlaceRepository;
import com.daejeongwang.uoscrazydaejeon.repository.VisitedPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Instant;
import java.time.Clock;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class VisitVerificationService {
    // GPS 위치 인증 비활성화 전에는 3번, 6번 회원만 검증을 우회했다.
    // private static final long GPS_VERIFICATION_BYPASS_MEMBER_ID_1 = 3L;
    // private static final long GPS_VERIFICATION_BYPASS_MEMBER_ID_2 = 6L;

    private final VisitedPlaceRepository visitedPlaceRepository;
    private final PlaceRepository placeRepository;
    private final MemberRepository memberRepository;
    private final PlaceProximityVerifier placeProximityVerifier;
    private final Clock clock;

    @Transactional
    // GPS 위치 인증 재활성화 시 기존 메서드 시그니처를 복구
    // public VisitVerificationResponse verifyVisit(Long memberId, Long placeId, VisitVerificationRequest request) {
    public VisitVerificationResponse verifyVisit(Long memberId, Long placeId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("회원이 없습니다."));

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new ResourceNotFoundException("장소가 없습니다."));

        // GPS 위치 인증 비활성화: 모든 회원이 위치 검증을 통과한다.
        // if (member.getId() != GPS_VERIFICATION_BYPASS_MEMBER_ID_1
        //         && member.getId() != GPS_VERIFICATION_BYPASS_MEMBER_ID_2) {
        //     placeProximityVerifier.verifyNearPlace(
        //             place,
        //             request.getLatitude(),
        //             request.getLongitude(),
        //             request.getAccuracy(),
        //             request.getMeasuredAt()
        //     );
        // }

        Instant now = clock.instant();
        ZoneId seoul = ZoneId.of("Asia/Seoul");
        LocalDate today = now.atZone(seoul).toLocalDate();
        Instant startOfDay = today.atStartOfDay(seoul).toInstant();
        Instant endOfDay = today.plusDays(1).atStartOfDay(seoul).toInstant();

        boolean alreadyVisited = visitedPlaceRepository
                .existsByMemberAndPlaceAndVisitedAtGreaterThanEqualAndVisitedAtLessThan(member, place, startOfDay, endOfDay);
        if(alreadyVisited){
            throw new ConflictException("이미 방문한 장소입니다.");
        }

        VisitedPlace visitedPlace = VisitedPlace.builder()
                .member(member)
                .visitedAt(now)
                .visitedDate(today)
                .place(place)
                .build();

        VisitedPlace savedVisitedPlace;
        try {
            savedVisitedPlace = visitedPlaceRepository.saveAndFlush(visitedPlace);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("이미 방문한 장소입니다.", e);
        }

        return VisitVerificationResponse.builder()
                .visitedPlaceId(savedVisitedPlace.getId())
                .placeId(place.getId())
                .visitedAt(savedVisitedPlace.getVisitedAt())
                .build();
    }


}
