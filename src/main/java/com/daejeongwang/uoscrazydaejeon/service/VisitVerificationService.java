package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.dto.request.VisitVerificationRequest;
import com.daejeongwang.uoscrazydaejeon.dto.response.VisitVerificationResponse;
import com.daejeongwang.uoscrazydaejeon.entity.Member;
import com.daejeongwang.uoscrazydaejeon.entity.Place;
import com.daejeongwang.uoscrazydaejeon.entity.Receipt;
import com.daejeongwang.uoscrazydaejeon.entity.VisitedPlace;
import com.daejeongwang.uoscrazydaejeon.exception.ConflictException;
import com.daejeongwang.uoscrazydaejeon.exception.ResourceNotFoundException;
import com.daejeongwang.uoscrazydaejeon.repository.MemberRepository;
import com.daejeongwang.uoscrazydaejeon.repository.PlaceRepository;
import com.daejeongwang.uoscrazydaejeon.repository.ReceiptRepository;
import com.daejeongwang.uoscrazydaejeon.repository.VisitedPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Instant;
import java.time.Clock;
import java.time.ZoneId;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VisitVerificationService {
    private static final long GPS_VERIFICATION_BYPASS_MEMBER_ID_1 = 3L;
    private static final long GPS_VERIFICATION_BYPASS_MEMBER_ID_2 = 6L;

    private final VisitedPlaceRepository visitedPlaceRepository;
    private final ReceiptRepository receiptRepository;
    private final PlaceRepository placeRepository;
    private final MemberRepository memberRepository;
    private final PlaceProximityVerifier placeProximityVerifier;
    private final Clock clock;

    @Transactional
    public VisitVerificationResponse verifyVisit(Long memberId, Long placeId, VisitVerificationRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("회원이 없습니다."));

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new ResourceNotFoundException("장소가 없습니다."));

        if (member.getId() != GPS_VERIFICATION_BYPASS_MEMBER_ID_1
                && member.getId() != GPS_VERIFICATION_BYPASS_MEMBER_ID_2) {
            if (request == null) {
                throw new IllegalArgumentException("위치정보가 필요합니다.");
            }
            placeProximityVerifier.verifyNearPlace(
                    place,
                    request.getLatitude(),
                    request.getLongitude(),
                    request.getAccuracy(),
                    request.getMeasuredAt()
            );
        }

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

        Receipt visitReceipt = null;
        if (isNonConsumerPlace(place)) {
            visitReceipt = receiptRepository.save(
                    Receipt.createVisitReceipt(savedVisitedPlace, now)
            );
        }

        VisitVerificationResponse.VisitVerificationResponseBuilder response = VisitVerificationResponse.builder()
                .visitedPlaceId(savedVisitedPlace.getId())
                .placeId(place.getId())
                .visitedAt(savedVisitedPlace.getVisitedAt());

        if (visitReceipt != null) {
            response.receiptId(visitReceipt.getId())
                    .receiptUuid(visitReceipt.getReceiptUuid())
                    .objectKey(visitReceipt.getObjectKey())
                    .requestedAt(visitReceipt.getRequestedAt())
                    .verifiedAt(visitReceipt.getVerifiedAt())
                    .verifyStatus(visitReceipt.getVerifyStatus())
                    .ocrStatus(visitReceipt.getOcrStatus())
                    .verificationType(visitReceipt.getVerificationType());
        }

        return response.build();
    }

    private boolean isNonConsumerPlace(Place place) {
        String categorySmall = place.getCategorySmall();
        if (categorySmall == null || categorySmall.isBlank()) {
            return false;
        }

        return Set.of(
                "공원", "생태공원", "산", "숲", "자연휴양림",
                "하천", "호수", "저수지", "산책로", "광장",
                "둘레길", "전망대", "도시근린공원"
        ).contains(categorySmall.trim());
    }


}
