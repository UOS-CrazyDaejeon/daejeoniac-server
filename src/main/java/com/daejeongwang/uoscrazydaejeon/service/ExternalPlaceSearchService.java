package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.client.PlaceSearchApiClient;
import com.daejeongwang.uoscrazydaejeon.dto.response.ExternalPlaceSearchResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.api.PlaceSearchApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExternalPlaceSearchService {

    private final PlaceSearchApiClient placeSearchApiClient;

    public List<ExternalPlaceSearchResponse> searchPlaces(String keyword) {
        PlaceSearchApiResponse response = placeSearchApiClient.searchByKeyword(keyword, 1, 10);

        if (response == null || response.getDocuments() == null) {
            return List.of();
        }

        return response.getDocuments().stream()
                .map(this::convertToResponse)
                .toList();
    }

    private ExternalPlaceSearchResponse convertToResponse(PlaceSearchApiResponse.Document document) {
        return ExternalPlaceSearchResponse.builder()
                .name(document.getPlaceName())
                .address(document.getAddressName())
                .roadAddress(document.getRoadAddressName())
                .phoneNumber(document.getPhone())
                .latitude(parseDouble(document.getY()))
                .longitude(parseDouble(document.getX()))
                .category(document.getCategoryName())
                .externalUrl(document.getPlaceUrl())
                .build();
    }

    private Double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return Double.valueOf(value);
    }
}