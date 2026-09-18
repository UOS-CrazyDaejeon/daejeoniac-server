package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.exception.ResourceNotFoundException;
import com.daejeongwang.uoscrazydaejeon.repository.MemberRepository;
import com.daejeongwang.uoscrazydaejeon.repository.PlaceClickLogRepository;
import com.daejeongwang.uoscrazydaejeon.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class PlaceClickLogService {

    private final PlaceRepository placeRepository;
    private final PlaceClickLogRepository placeClickLogRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void saveClickLog(Long memberId, Long placeId) {
        if (!placeRepository.existsById(placeId)) {
            throw new ResourceNotFoundException("장소를 찾을 수 없습니다.");
        }
        if (!memberRepository.existsById(memberId)) {
            throw new ResourceNotFoundException("회원을 찾을 수 없습니다.");
        }

        LocalDateTime clickedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        placeClickLogRepository.insertIgnore(memberId, placeId, clickedAt);
    }

    public long getClickCountByPlaceId(Long placeId) {
        if (!placeRepository.existsById(placeId)) {
            throw new ResourceNotFoundException("장소를 찾을 수 없습니다.");
        }

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        return placeClickLogRepository.countByPlace_IdAndClickedAtGreaterThanEqualAndClickedAtLessThan(
                placeId,
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );
    }

}
