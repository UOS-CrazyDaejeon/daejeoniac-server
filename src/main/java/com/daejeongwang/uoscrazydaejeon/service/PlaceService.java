package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.client.DaejeonRestaurantApiClient;
import com.daejeongwang.uoscrazydaejeon.client.DaejeonShoppingApiClient;
import com.daejeongwang.uoscrazydaejeon.client.DaejeonTourspotApiClient;
import com.daejeongwang.uoscrazydaejeon.dto.response.PlaceResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.api.DaejeonRestaurantItemResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.api.DaejeonShoppingItemResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.api.DaejeonTourspotItemResponse;
import com.daejeongwang.uoscrazydaejeon.entity.Place;
import com.daejeongwang.uoscrazydaejeon.repository.PlaceRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class PlaceService {

    private final DaejeonShoppingApiClient daejeonShoppingApiClient;
    private final DaejeonTourspotApiClient daejeonTourspotApiClient;
    private final DaejeonRestaurantApiClient daejeonRestaurantApiClient;

    private final PlaceRepository placeRepository;

    // Admin Place API Service
    @Transactional
    public void syncAllPlaces() {
        syncTourspotPlaces();
        syncShoppingPlaces();
        syncRestaurantPlaces();
    }

    public void syncShoppingPlaces() {
        List<DaejeonShoppingItemResponse> response = daejeonShoppingApiClient.fetchShopping();

        for(DaejeonShoppingItemResponse shopping : response) {
            Place place = convertShoppingToEntity(shopping);

            if(!placeRepository.existsByPlaceNameAndPlaceAddressAndCategoryLarge(place.getPlaceName(), place.getPlaceAddress(), place.getCategoryLarge()))
                placeRepository.save(place);
        }
    }

    public void syncTourspotPlaces() {
        List<DaejeonTourspotItemResponse> response = daejeonTourspotApiClient.fetchTourspot();

        for(DaejeonTourspotItemResponse tourspot : response) {
            Place place = convertTourspotToEntity(tourspot);

            if(!placeRepository.existsByPlaceNameAndPlaceAddressAndCategoryLarge(place.getPlaceName(), place.getPlaceAddress(), place.getCategoryLarge()))
                placeRepository.save(place);
        }
    }

    public void syncRestaurantPlaces() {
        List<DaejeonRestaurantItemResponse> response = daejeonRestaurantApiClient.fetchRestaurants();

        for(DaejeonRestaurantItemResponse restaurant : response) {
            Place place = convertRestaurantToEntity(restaurant);

            if(!placeRepository.existsByPlaceNameAndPlaceAddressAndCategoryLarge(place.getPlaceName(), place.getPlaceAddress(), place.getCategoryLarge()))
                placeRepository.save(place);
        }
    }

    private Place convertShoppingToEntity(DaejeonShoppingItemResponse dto) {
        String[] GuDong = extractGuDong(dto.getShppgAddr());

        return Place.builder()
                .placeName(dto.getShppgNm())
                .placeDescription(dto.getShppgIntrd())
                .placeAddress(dto.getShppgAddr())
                .latitude(parseCoordinate(dto.getMapLat()))
                .longitude(parseCoordinate(dto.getMapLot()))
                .gu(GuDong != null && GuDong.length > 1 ? GuDong[1] : null)
                .dong(GuDong != null && GuDong.length > 2 ? GuDong[2] : null)
                .categoryLarge("쇼핑")
                .categoryMedium(null)
                .categorySmall(null)
                .build();
    }

    private Place convertTourspotToEntity(DaejeonTourspotItemResponse dto) {
        String[] GuDong = extractGuDong(dto.getTourspotAddr());

        return Place.builder()
                .placeName(dto.getTourspotNm())
                .placeDescription(dto.getTourspotSumm())
                .placeAddress(dto.getTourspotAddr())
                .latitude(parseCoordinate(dto.getMapLat()))
                .longitude(parseCoordinate(dto.getMapLot()))
                .gu(GuDong != null && GuDong.length > 1 ? GuDong[1] : null)
                .dong(GuDong != null && GuDong.length > 2 ? GuDong[2] : null)
                .categoryLarge("관광지")
                .categoryMedium(null)
                .categorySmall(null)
                .build();
    }

    private Place convertRestaurantToEntity(DaejeonRestaurantItemResponse dto) {
        String[] GuDong = extractGuDong(dto.getRoadAddress());

        return Place.builder()
                .placeName(dto.getRestaurantName())
                .placeDescription(null)
                .placeAddress(dto.getRoadAddress())
                .latitude(null)
                .longitude(null)
                .gu(GuDong != null && GuDong.length > 1 ? GuDong[1] : null)
                .dong(GuDong != null && GuDong.length > 2 ? GuDong[2] : null)
                .categoryLarge("일반 음식점")
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

    // Place 조회 API Service
    public List<PlaceResponse> findAllPlaces(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return placeRepository.findAll(pageable)
                .stream()
                .map(PlaceResponse::from)
                .toList();
    }

    public PlaceResponse getPlaceById(Long placeId) {
        Place place = placeRepository.findByPlaceId(placeId)
                .orElseThrow(() -> new RuntimeException("장소를 찾을 수 없습니다."));

        return PlaceResponse.from(place);
    }

    public Page<PlaceResponse> getNearbyPlacesByPlaceId(
            Long placeId,
            Double radius,
            int page, int size
    ) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new RuntimeException("장소를 찾을 수 없습니다."));

        if(place.getLatitude() == null || place.getLongitude() == null) {
            throw new RuntimeException("해당 장소의 좌표 정보가 없습니다.");
        }

        Pageable pageable = PageRequest.of(page, size);

        return placeRepository.findNearbyPlaces(
                        placeId,
                        place.getLatitude(),
                        place.getLongitude(),
                        radius,
                        pageable
                )
                .map(PlaceResponse::from);
    }

}
