package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.client.CongestionApiClient;
import com.daejeongwang.uoscrazydaejeon.dto.response.api.CongestionItemResponse;
import com.daejeongwang.uoscrazydaejeon.entity.Congestion;
import com.daejeongwang.uoscrazydaejeon.entity.Place;
import com.daejeongwang.uoscrazydaejeon.repository.CongestionRepository;
import com.daejeongwang.uoscrazydaejeon.repository.PlaceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CongestionService {

    private final CongestionApiClient congestionApiClient;
    private final CongestionRepository congestionRepository;
    private final PlaceRepository placeRepository;

    @Transactional
    public void syncCongestions() {
        List<CongestionItemResponse> response = congestionApiClient.fetchAllCongestions();

        for(CongestionItemResponse congestionItemResponse : response) {
            Congestion congestion = convertCongestionToEntity(congestionItemResponse);

            if(congestion == null)
                continue;

            if(!congestionRepository.existsByPlaceAndDate(congestion.getPlace(), congestion.getDate())) {
                congestionRepository.save(congestion);
            }
        }
    }

    public Congestion convertCongestionToEntity(CongestionItemResponse dto) {
        Place place = placeRepository.findByPlaceName(dto.getTAtsNm()).orElse(null);

        if(place == null)
            return null;

        return Congestion.builder()
                .place(place)
                .placeName(dto.getTAtsNm())
                .gu(dto.getSignguNm())
                .date(dto.getBaseYmd())
                .congestionRate(Double.valueOf(dto.getCnctrRate()))
                .build();
    }


    private Double parseCongestionRate(String value) {
        if(value == null || value.isBlank())
            return null;

        return Double.valueOf(value);
    }

    // 특정 장소에 대한 congestion 전체 조회



}
