package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.client.AiServerClient;
import com.daejeongwang.uoscrazydaejeon.dto.request.NextPlacesRecommendationRequest;
import com.daejeongwang.uoscrazydaejeon.dto.request.RecommendationPlaceRequest;
import com.daejeongwang.uoscrazydaejeon.dto.request.SimilarRecommendationRequest;
import com.daejeongwang.uoscrazydaejeon.dto.response.AiNextPlacesRecommendationResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.AiRecommendationResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.AiSimilarRecommendationResponse;
import com.daejeongwang.uoscrazydaejeon.entity.Place;
import com.daejeongwang.uoscrazydaejeon.exception.ResourceNotFoundException;
import com.daejeongwang.uoscrazydaejeon.repository.CongestionRepository;
import com.daejeongwang.uoscrazydaejeon.repository.PlaceRepository;
import com.daejeongwang.uoscrazydaejeon.repository.VisitedPlaceRepository;
import com.daejeongwang.uoscrazydaejeon.repository.VisitorCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private static final double RADIUS = 1000.0;

    private final PlaceRepository placeRepository;
    private final VisitedPlaceRepository visitedPlaceRepository;
    private final CongestionRepository congestionRepository;
    private final VisitorCountRepository visitorCountRepository;
    private final AiServerClient aiServerClient;

    public AiSimilarRecommendationResponse recommendSimilarPlaces(Long placeId) {
        SimilarRecommendationRequest request = createSimilarRecommendationRequest(placeId);

        return aiServerClient.requestSimilarRecommendations(request);
    }

    public AiNextPlacesRecommendationResponse recommendNextPlaces(Long memberId, Long placeId) {
        NextPlacesRecommendationRequest request = createNextPlacesRecommendationRequest(memberId, placeId);

        return aiServerClient.requestNextPlacesRecommendations(request);
    }

    private SimilarRecommendationRequest createSimilarRecommendationRequest(Long placeId) {
        Place selectedPlace = findPlace(placeId);
        List<RecommendationPlaceRequest> nearbyPlaces = findNearbyPlaces(selectedPlace)
                .stream()
                .map(place -> toRecommendationPlaceRequest(place, null))
                .toList();

        return new SimilarRecommendationRequest(
                toRecommendationPlaceRequest(selectedPlace, null),
                nearbyPlaces
        );
    }

    private NextPlacesRecommendationRequest createNextPlacesRecommendationRequest(Long memberId, Long placeId) {
        Place selectedPlace = findPlace(placeId);
        List<RecommendationPlaceRequest> nearbyPlaces = findNearbyPlaces(selectedPlace)
                .stream()
                .map(place -> toRecommendationPlaceRequest(place, null))
                .toList();
        List<RecommendationPlaceRequest> visitedPlaces = visitedPlaceRepository.findAllByMember_IdOrderByVisitedAtDesc(memberId)
                .stream()
                .map(visitedPlace -> toRecommendationPlaceRequest(
                        visitedPlace.getPlace(),
                        visitedPlace.getVisitedAt()
                ))
                .toList();

        return new NextPlacesRecommendationRequest(
                toRecommendationPlaceRequest(selectedPlace, null),
                nearbyPlaces,
                visitedPlaces
        );
    }

    private Place findPlace(Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new ResourceNotFoundException("장소를 찾을 수 없습니다."));

        if (place.getLatitude() == null || place.getLongitude() == null) {
            throw new IllegalStateException("해당 장소의 좌표 정보가 없습니다.");
        }

        return place;
    }

    private List<Place> findNearbyPlaces(Place place) {
        return placeRepository.findAllNearbyPlacesWithoutPaging(
                place.getId(),
                place.getLatitude(),
                place.getLongitude(),
                RADIUS
        );
    }

    private RecommendationPlaceRequest toRecommendationPlaceRequest(Place place, LocalDateTime visitedAt) {
        Double congestionRate = congestionRepository.findFirstByPlace_IdOrderByDateDesc(place.getId())
                .map(congestion -> congestion.getCongestionRate())
                .orElse(null);
        Long visitorCount = visitorCountRepository.findFirstByPlace_IdOrderByDateDesc(place.getId())
                .map(visitor -> visitor.getVisitorCount())
                .orElse(null);

        return RecommendationPlaceRequest.from(place, congestionRate, visitorCount, visitedAt);
    }
}
