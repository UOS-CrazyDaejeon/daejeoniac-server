package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.dto.RecommendationSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecommendationSessionService {
    private static final String SESSION_KEY_PREFIX = "recommendation:session:";
    private static final Duration SESSION_TTL = Duration.ofDays(1);

    private final RedisTemplate<String, Object> redisTemplate;

    public String saveSession(RecommendationSession session) {
        String sessionId = UUID.randomUUID().toString();

        String sessionKey = SESSION_KEY_PREFIX + sessionId;

        redisTemplate.opsForValue().set(sessionKey, session, SESSION_TTL);

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
}

