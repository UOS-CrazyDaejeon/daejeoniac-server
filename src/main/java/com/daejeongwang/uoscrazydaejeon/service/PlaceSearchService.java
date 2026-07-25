package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.client.PlaceSearchApiClient;
import com.daejeongwang.uoscrazydaejeon.dto.response.PlaceSearchResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.api.PlaceSearchApiResponse;
import com.daejeongwang.uoscrazydaejeon.entity.Place;
import com.daejeongwang.uoscrazydaejeon.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceSearchService {

    private final PlaceSearchApiClient placeSearchApiClient;

    private final PlaceRepository placeRepository;

    public void syncPlaces() {
        List<PlaceSearchResponse> response = searchPlaces("대전 중구");

        for (PlaceSearchResponse placeSearchResponse : response) {
            Place place = convertToEntity(placeSearchResponse);

            if(!placeRepository.existsByPlaceNameAndPlaceAddressAndCategoryLarge(place.getPlaceName(), place.getPlaceAddress(), place.getCategoryLarge()))
                placeRepository.save(place);
        }
    }

    private Place convertToEntity(PlaceSearchResponse placeSearchResponse) {

        // TODO : tag, placeDescription은 일단 null 처리, 추후 수정 필요
        return Place.builder()
                .placeName(placeSearchResponse.getName())
                .tag(null)
                .placeDescription(null)
                .placeAddress(placeSearchResponse.getAddress())
                .latitude(placeSearchResponse.getLatitude())
                .longitude(placeSearchResponse.getLongitude())
                .gu(extractGu(placeSearchResponse.getAddress()))
                .dong(extractDong(placeSearchResponse.getAddress()))
                .categoryLarge(extractCategory(placeSearchResponse.getCategory(), 0))
                .categoryMedium(extractCategory(placeSearchResponse.getCategory(), 1))
                .categorySmall(extractCategory(placeSearchResponse.getCategory(), 2))
                .build();
    }

    private String extractGu(String address) {
        if(address == null || address.isBlank())
            return null;

        for(String part : address.split("\\s+"))
            if (part.endsWith("구"))
                return part;

        return null;
    }

    private String extractDong(String address) {
        if(address == null || address.isBlank())
            return null;

        for(String part : address.split("\\s+"))
            if (part.endsWith("동"))
                return part;

        return null;
    }

    // 카테고리 나누기
    private String extractCategory(String category, int index) {
        if(category == null || category.isBlank())
            return null;

        String[] parts = category.split(">");

        if(parts.length <= index)
            return null;

        return parts[index].trim();
    }


    // 밑부터 장소 search 기능
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
