package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.dto.RecommendationSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecommendationSessionService {
    private static final String SESSION_KEY_PREFIX = "recommendation:session:";
    private static final String USER_SESSION_KEY_PREFIX = "recommendation:user:";
    private static final String USER_SESSION_KEY_SUFFIX = ":sessions";
    private static final Duration SESSION_TTL = Duration.ofDays(1);

    private final RedisTemplate<String, Object> redisTemplate;

    public String saveSession(RecommendationSession session) {
        String sessionId = UUID.randomUUID().toString();

        String sessionKey = SESSION_KEY_PREFIX + sessionId;
        String userSessionKey = USER_SESSION_KEY_PREFIX + session.getMemberId() + USER_SESSION_KEY_SUFFIX;

        Instant expiresAt = Instant.now().plus(SESSION_TTL);

        redisTemplate.opsForValue().set(sessionKey, session, SESSION_TTL);

        redisTemplate.opsForZSet().add(userSessionKey, sessionId, expiresAt.toEpochMilli());
        redisTemplate.expire(userSessionKey, SESSION_TTL);

        return sessionId;
    }

    public RecommendationSession getSession(String sessionId) {
        String key = SESSION_KEY_PREFIX + sessionId;

        Object value = redisTemplate.opsForValue().get(key);

        if(value == null) {
            return null;
        }

        return (RecommendationSession) value;
    }

    private Set<Object> getActiveSessionIds(Long memberId) {
        String userSessionKey = USER_SESSION_KEY_PREFIX + memberId + USER_SESSION_KEY_SUFFIX;

        long now = Instant.now().toEpochMilli();

        redisTemplate.opsForZSet().removeRangeByScore(userSessionKey, 0, now);

        return redisTemplate.opsForZSet().range(userSessionKey, 0, -1);


    }

    public void validateRecommendedPlace(Long memberId, Long placeId) {
        Set<Object> sessionIds = getActiveSessionIds(memberId);

        if(sessionIds == null || sessionIds.isEmpty()) {
            throw new IllegalArgumentException("유효한 추천 세션이 존재하지 않습니다.");
        }

        for(Object sessionIdValue : sessionIds) {
            String sessionId = sessionIdValue.toString();
            RecommendationSession session = getSession(sessionId);

            if(session.getRecommendations() == null) {
                continue;
            }

            boolean recommended = session.getRecommendations().stream()
                    .anyMatch(recommendation -> recommendation != null && placeId.equals(recommendation.getPlaceId()));

            if(recommended) {
                return;
            }
        }

        throw new IllegalArgumentException("추천 세션에 포함되지 않은 장소입니다.");

    }
}

