package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.client.DaejeonShoppingApiClient;
import com.daejeongwang.uoscrazydaejeon.client.DaejeonTourspotApiClient;
import com.daejeongwang.uoscrazydaejeon.dto.response.api.DaejeonShoppingItemResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.api.DaejeonTourspotItemResponse;
import com.daejeongwang.uoscrazydaejeon.entity.Place;
import com.daejeongwang.uoscrazydaejeon.repository.PlaceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class PlaceService {

    private final DaejeonShoppingApiClient daejeonShoppingApiClient;
    private final DaejeonTourspotApiClient daejeonTourspotApiClient;

    private final PlaceRepository placeRepository;

    @Transactional
    public void syncAllPlaces() {
        syncTourspotPlaces();
        syncShoppingPlaces();
    }

    public void syncShoppingPlaces() {
        List<DaejeonShoppingItemResponse> response = daejeonShoppingApiClient.fetchShopping();

        for(DaejeonShoppingItemResponse shopping : response) {
            Place place = convertShoppingToEntity(shopping);
            placeRepository.save(place);
        }
    }

    public void syncTourspotPlaces() {
        List<DaejeonTourspotItemResponse> response = daejeonTourspotApiClient.fetchTourspot();

        for(DaejeonTourspotItemResponse tourspot : response) {
            Place place = convertTourspotToEntity(tourspot);
            placeRepository.save(place);
        }
    }

    private Place convertShoppingToEntity(DaejeonShoppingItemResponse dto) {
        String[] GuDong = extractGuDong(dto.getShppgDtlAddr());

        return Place.builder()
                .placeName(dto.getShppgNm())
                .placeDescription(dto.getShppgIntrd())
                .placeAddress(dto.getShppgAddr())
                .latitude(parseCoordinate(dto.getMapLat()))
                .longitude(parseCoordinate(dto.getMapLot()))
                .gu(GuDong[1])
                .dong(GuDong[2])
                .categoryLarge("쇼핑")
                .categoryMedium(null)
                .categorySmall(null)
                .build();
    }

    private Place convertTourspotToEntity(DaejeonTourspotItemResponse dto) {
        String[] GuDong = extractGuDong(dto.getTourspotDtlAddr());

        return Place.builder()
                .placeName(dto.getTourspotNm())
                .placeDescription(dto.getTourspotSumm())
                .placeAddress(dto.getTourspotAddr())
                .latitude(parseCoordinate(dto.getMapLat()))
                .longitude(parseCoordinate(dto.getMapLot()))
                .gu(GuDong[1])
                .dong(GuDong[2])
                .categoryLarge("관광지")
                .categoryMedium(null)
                .categorySmall(null)
                .build();
    }

    private Double parseCoordinate(String value) {
        if(value == null || value.isBlank() || value.equals("0"))
            return null;

        return Double.valueOf(value);
    }

    private String[] extractGuDong(String address) {
        if(address == null || address.isBlank())
            return null;

        // 공백을 기준으로 split하여 배열로 반환
        String[] parts = address.split("\\s+");

        return parts;
    }


}
