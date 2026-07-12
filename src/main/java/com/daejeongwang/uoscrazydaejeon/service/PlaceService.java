package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.client.RestaurantApiClient;
import com.daejeongwang.uoscrazydaejeon.client.ShoppingApiClient;
import com.daejeongwang.uoscrazydaejeon.client.TourspotApiClient;
import com.daejeongwang.uoscrazydaejeon.dto.response.api.RestaurantItemResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.api.ShoppingItemResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.api.TourspotItemResponse;
import com.daejeongwang.uoscrazydaejeon.entity.Place;
import com.daejeongwang.uoscrazydaejeon.repository.PlaceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class PlaceService {

    private final ShoppingApiClient shoppingApiClient;
    private final TourspotApiClient tourspotApiClient;
    private final RestaurantApiClient restaurantApiClient;

    private final PlaceRepository placeRepository;

    @Transactional
    public void syncAllPlaces() {
        syncTourspotPlaces();
        syncShoppingPlaces();
        syncRestaurantPlaces();
    }

    public void syncShoppingPlaces() {
        List<ShoppingItemResponse> response = shoppingApiClient.fetchShopping();

        for(ShoppingItemResponse shopping : response) {
            Place place = convertShoppingToEntity(shopping);

            if(!placeRepository.existsByPlaceNameAndPlaceAddressAndCategoryLarge(place.getPlaceName(), place.getPlaceAddress(), place.getCategoryLarge()))
                placeRepository.save(place);
        }
    }

    public void syncTourspotPlaces() {
        List<TourspotItemResponse> response = tourspotApiClient.fetchTourspot();

        for(TourspotItemResponse tourspot : response) {
            Place place = convertTourspotToEntity(tourspot);

            if(!placeRepository.existsByPlaceNameAndPlaceAddressAndCategoryLarge(place.getPlaceName(), place.getPlaceAddress(), place.getCategoryLarge()))
                placeRepository.save(place);
        }
    }

    public void syncRestaurantPlaces() {
        List<RestaurantItemResponse> response = restaurantApiClient.fetchRestaurants();

        for(RestaurantItemResponse restaurant : response) {
            Place place = convertRestaurantToEntity(restaurant);

            if(!placeRepository.existsByPlaceNameAndPlaceAddressAndCategoryLarge(place.getPlaceName(), place.getPlaceAddress(), place.getCategoryLarge()))
                placeRepository.save(place);
        }
    }

    private Place convertShoppingToEntity(ShoppingItemResponse dto) {
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

    private Place convertTourspotToEntity(TourspotItemResponse dto) {
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

    private Place convertRestaurantToEntity(RestaurantItemResponse dto) {
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


}
