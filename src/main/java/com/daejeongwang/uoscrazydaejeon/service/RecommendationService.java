package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.client.AiServerClient;
import com.daejeongwang.uoscrazydaejeon.dto.RecommendationSession;
import com.daejeongwang.uoscrazydaejeon.dto.request.NaturalSearchPlaceRequest;
import com.daejeongwang.uoscrazydaejeon.dto.request.NaturalSearchRecommendationRequest;
import com.daejeongwang.uoscrazydaejeon.dto.request.NextPlacesRecommendationRequest;
import com.daejeongwang.uoscrazydaejeon.dto.request.RecommendationPlaceRequest;
import com.daejeongwang.uoscrazydaejeon.dto.request.SimilarRecommendationRequest;
import com.daejeongwang.uoscrazydaejeon.dto.response.AiNextPlacesRecommendationResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.AiRecommendationResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.AiSimilarRecommendationResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.RecommendedPlaceResponse;
import com.daejeongwang.uoscrazydaejeon.entity.Place;
import com.daejeongwang.uoscrazydaejeon.exception.ResourceNotFoundException;
import com.daejeongwang.uoscrazydaejeon.repository.CongestionRepository;
import com.daejeongwang.uoscrazydaejeon.repository.PlaceRepository;
import com.daejeongwang.uoscrazydaejeon.repository.VisitedPlaceRepository;
import com.daejeongwang.uoscrazydaejeon.repository.VisitorCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private static final double RADIUS = 1000.0;
    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    private final PlaceRepository placeRepository;
    private final VisitedPlaceRepository visitedPlaceRepository;
    private final CongestionRepository congestionRepository;
    private final VisitorCountRepository visitorCountRepository;
    private final AiServerClient aiServerClient;
    private final RecommendationSessionService recommendationSessionService;
    private final Clock clock;

    public AiSimilarRecommendationResponse recommendSimilarPlaces(Long memberId, Long placeId) {
        SimilarRecommendationRequest request = createSimilarRecommendationRequest(placeId);

        AiSimilarRecommendationResponse response = aiServerClient.requestSimilarRecommendations(request);
        if (response == null || response.getSimilarPlaces() == null) {
            throw new IllegalStateException("AI 추천 응답이 올바르지 않습니다.");
        }

        RecommendationSession session = RecommendationSession.builder()
                .memberId(memberId)
                .parentPlaceId(placeId)
                .recommendations(toSessionRecommendations(response.getSimilarPlaces()))
                .createdAt(LocalDateTime.now())
                .build();

        UUID sessionId = recommendationSessionService.saveSession(session);
        response.setSessionId(sessionId);

        return response;
    }

    public AiNextPlacesRecommendationResponse recommendNextPlaces(Long memberId, Long placeId) {
        NextPlacesRecommendationRequest request = createNextPlacesRecommendationRequest(memberId, placeId);

        AiNextPlacesRecommendationResponse response = aiServerClient.requestNextPlacesRecommendations(request);
        if (response == null || response.getNextPlaces() == null) {
            throw new IllegalStateException("AI 추천 응답이 올바르지 않습니다.");
        }

        RecommendationSession session = RecommendationSession.builder()
                .memberId(memberId)
                .parentPlaceId(placeId)
                .recommendations(toSessionRecommendations(response.getNextPlaces()))
                .createdAt(LocalDateTime.now())
                .build();

        UUID sessionId = recommendationSessionService.saveSession(session);
        response.setSessionId(sessionId);

        return response;
    }

    public Object recommendNaturalSearch(Long placeId, String query, Integer topK) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("검색어가 필요합니다.");
        }

        if (topK == null || topK <= 0) {
            throw new IllegalArgumentException("top_k는 1 이상이어야 합니다.");
        }

        NaturalSearchRecommendationRequest request = createNaturalSearchRecommendationRequest(
                placeId,
                query,
                topK
        );

        Object response = aiServerClient.requestNaturalSearchRecommendations(request);
        if (response == null) {
            throw new IllegalStateException("AI 추천 응답이 올바르지 않습니다.");
        }

        return response;
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
        LocalDate today = LocalDate.now(clock.withZone(SEOUL));
        List<RecommendationPlaceRequest> visitedPlaces = visitedPlaceRepository
                .findAllByMember_IdAndVisitedDateOrderByVisitedAtDesc(memberId, today)
                .stream()
                .map(visitedPlace -> toRecommendationPlaceRequest(
                        visitedPlace.getPlace(),
                        LocalDateTime.ofInstant(visitedPlace.getVisitedAt(), SEOUL)
                ))
                .toList();

        return new NextPlacesRecommendationRequest(
                toRecommendationPlaceRequest(selectedPlace, null),
                nearbyPlaces,
                visitedPlaces
        );
    }

    private NaturalSearchRecommendationRequest createNaturalSearchRecommendationRequest(
            Long placeId,
            String query,
            Integer topK
    ) {
        Place selectedPlace = findPlace(placeId);
        List<NaturalSearchPlaceRequest> nearbyPlaces = findNearbyPlaces(selectedPlace)
                .stream()
                .map(this::toNaturalSearchPlaceRequest)
                .toList();

        return new NaturalSearchRecommendationRequest(
                nearbyPlaces,
                query,
                topK
        );
    }

    private NaturalSearchPlaceRequest toNaturalSearchPlaceRequest(Place place) {
        Double congestionRate = congestionRepository.findFirstByPlace_IdOrderByDateDesc(place.getId())
                .map(congestion -> congestion.getCongestionRate())
                .orElse(null);
        Long visitorCount = visitorCountRepository.findFirstByPlace_IdOrderByDateDesc(place.getId())
                .map(visitor -> visitor.getVisitorCount())
                .orElse(null);

        return NaturalSearchPlaceRequest.from(place, congestionRate, visitorCount);
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

    private List<RecommendedPlaceResponse> toSessionRecommendations(List<AiRecommendationResponse> recommendations) {
        return recommendations.stream()
                .map(item -> RecommendedPlaceResponse.builder()
                        .placeId(item.getPlaceId())
                        .reason(item.getRecommendationReason())
                        .build())
                .toList();
    }
}
