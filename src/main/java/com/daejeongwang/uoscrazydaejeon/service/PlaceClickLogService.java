package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.entity.Place;
import com.daejeongwang.uoscrazydaejeon.entity.PlaceClickLog;
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

    // TODO : 현재 로그인 사용자의 ID를 파라미터로 받기
    @Transactional
    public void saveClickLog(Long placeId) {
        Place place = placeRepository.findByPlaceId(placeId)
                .orElseThrow(() -> new RuntimeException("장소를 찾을 수 없습니다."));

        PlaceClickLog clickLog = PlaceClickLog.builder()
                .place(place)
                .member(null)
                .build();

        placeClickLogRepository.save(clickLog);
    }

    public long getClickCountByPlaceId(Long placeId) {
        return placeClickLogRepository.countByPlace_PlaceId(placeId);
    }

}
