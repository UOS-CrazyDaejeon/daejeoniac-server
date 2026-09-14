package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.dto.RecommendationSession;
import com.daejeongwang.uoscrazydaejeon.dto.response.RecommendationSessionPlaceResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.RecommendationSessionResponse;
import com.daejeongwang.uoscrazydaejeon.entity.Place;
import com.daejeongwang.uoscrazydaejeon.repository.PlaceClickLogRepository;
import com.daejeongwang.uoscrazydaejeon.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationSessionService {
    private static final String SESSION_KEY_PREFIX = "recommendation:session:";
    private static final Duration SESSION_TTL = Duration.ofDays(1);
    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    private final RedisTemplate<String, Object> redisTemplate;
    private final PlaceRepository placeRepository;
    private final PlaceClickLogRepository placeClickLogRepository;
    private final Clock clock;

    public UUID saveSession(RecommendationSession session) {
        UUID sessionId = UUID.randomUUID();

        String sessionKey = SESSION_KEY_PREFIX + sessionId;

        redisTemplate.opsForValue().set(sessionKey, session, SESSION_TTL);

        return sessionId;
    }

    public RecommendationSessionResponse getSession(Long memberId, UUID sessionId) {
        String key = SESSION_KEY_PREFIX + sessionId;

        Object value = redisTemplate.opsForValue().get(key);

        if(value == null) {
            return null;
        }

        RecommendationSession session = (RecommendationSession) value;

        if (!session.getMemberId().equals(memberId)) {
            return null;
        }

        List<Long> placeIds = session.getRecommendations().stream()
                .map(recommendation -> recommendation.getPlaceId())
                .distinct()
                .toList();
        Map<Long, Place> placesById = placeRepository.findAllById(placeIds).stream()
                .collect(Collectors.toMap(Place::getId, Function.identity()));

        LocalDate today = LocalDate.now(clock.withZone(SEOUL));
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime startOfNextDay = today.plusDays(1).atStartOfDay();
        Map<Long, Long> viewerCountsByPlaceId = placeIds.isEmpty()
                ? Map.of()
                : placeClickLogRepository.countByPlaceIdsAndClickedAtBetween(
                                placeIds,
                                startOfDay,
                                startOfNextDay
                        ).stream()
                        .collect(Collectors.toMap(
                                PlaceClickLogRepository.PlaceClickCount::getPlaceId,
                                PlaceClickLogRepository.PlaceClickCount::getViewerCount
                        ));

        List<RecommendationSessionPlaceResponse> recommendations = session.getRecommendations().stream()
                .map(recommendation -> {
                    Place place = placesById.get(recommendation.getPlaceId());
                    return new RecommendationSessionPlaceResponse(
                            recommendation.getPlaceId(),
                            place == null ? null : place.getPlaceName(),
                            place == null ? null : place.getTag(),
                            viewerCountsByPlaceId.getOrDefault(recommendation.getPlaceId(), 0L),
                            recommendation.getReason()
                    );
                })
                .toList();

        return new RecommendationSessionResponse(
                sessionId,
                session.getParentPlaceId(),
                session.getCreatedAt(),
                recommendations
        );
    }
}

