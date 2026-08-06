package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.entity.Member;
import com.daejeongwang.uoscrazydaejeon.entity.Place;
import com.daejeongwang.uoscrazydaejeon.entity.PlaceClickLog;
import com.daejeongwang.uoscrazydaejeon.exception.ResourceNotFoundException;
import com.daejeongwang.uoscrazydaejeon.repository.MemberRepository;
import com.daejeongwang.uoscrazydaejeon.repository.PlaceClickLogRepository;
import com.daejeongwang.uoscrazydaejeon.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceClickLogService {

    private final PlaceRepository placeRepository;
    private final PlaceClickLogRepository placeClickLogRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void saveClickLog(Long memberId, Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new ResourceNotFoundException("장소를 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));

        PlaceClickLog clickLog = PlaceClickLog.builder()
                .place(place)
                .member(member)
                .build();

        placeClickLogRepository.save(clickLog);
    }

    public long getClickCountByPlaceId(Long placeId) {
        if (!placeRepository.existsById(placeId)) {
            throw new ResourceNotFoundException("장소를 찾을 수 없습니다.");
        }

        return placeClickLogRepository.countByPlace_Id(placeId);
    }

}
