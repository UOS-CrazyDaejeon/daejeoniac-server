package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.client.PlaceSearchApiClient;
import com.daejeongwang.uoscrazydaejeon.dto.response.PlaceSearchResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.api.PlaceSearchApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceSearchService {

    private final PlaceSearchApiClient placeSearchApiClient;

    public List<PlaceSearchResponse> searchPlaces(String keyword) {
        PlaceSearchApiResponse response = placeSearchApiClient.searchByKeyword(keyword, 1, 15);

        if (response == null || response.getDocuments() == null) {
            return List.of();
        }

        return response.getDocuments().stream()
                .map(this::convertToResponse)
                .toList();
    }

    private PlaceSearchResponse convertToResponse(PlaceSearchApiResponse.Document document) {
        return PlaceSearchResponse.builder()
                .name(document.getPlaceName())
                .address(document.getAddressName())
                .roadAddress(document.getRoadAddressName())
                .phoneNumber(document.getPhone())
                .latitude(parseDouble(document.getY()))
                .longitude(parseDouble(document.getX()))
                .category(document.getCategoryName())
                .url(document.getPlaceUrl())
                .build();
    }

    private Double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return Double.valueOf(value);
    }
}
