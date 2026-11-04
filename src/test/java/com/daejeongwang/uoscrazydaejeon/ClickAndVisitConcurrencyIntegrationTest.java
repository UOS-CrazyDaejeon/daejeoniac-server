package com.daejeongwang.uoscrazydaejeon;

import com.daejeongwang.uoscrazydaejeon.dto.request.VisitVerificationRequest;
import com.daejeongwang.uoscrazydaejeon.entity.Member;
import com.daejeongwang.uoscrazydaejeon.entity.Place;
import com.daejeongwang.uoscrazydaejeon.exception.ConflictException;
import com.daejeongwang.uoscrazydaejeon.repository.MemberRepository;
import com.daejeongwang.uoscrazydaejeon.repository.PlaceClickLogRepository;
import com.daejeongwang.uoscrazydaejeon.repository.PlaceRepository;
import com.daejeongwang.uoscrazydaejeon.repository.VisitedPlaceRepository;
import com.daejeongwang.uoscrazydaejeon.service.PlaceClickLogService;
import com.daejeongwang.uoscrazydaejeon.service.PlaceProximityVerifier;
import com.daejeongwang.uoscrazydaejeon.service.VisitVerificationService;
import com.daejeongwang.uoscrazydaejeon.util.DistanceCalculator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 실제 MySQL에서 서비스의 동시 요청 정합성을 검증한다.
 *
 * Docker가 실행 가능한 환경에서 실행:
 *   ./gradlew test --tests '*ClickAndVisitConcurrencyIntegrationTest'
 *
 * @DataJpaTest의 기본 테스트 트랜잭션을 끄고, 별도 스레드의 서비스
 * 트랜잭션이 진짜 commit되도록 한다. H2에서는 INSERT IGNORE와
 * MySQL generated column을 동일하게 검증할 수 없다.
 */
@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create", // disposable MySQL container is removed after tests
        "spring.jpa.show-sql=false"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({
        PlaceClickLogService.class,
        VisitVerificationService.class,
        PlaceProximityVerifier.class,
        DistanceCalculator.class,
        ClickAndVisitConcurrencyIntegrationTest.TestClockConfig.class
})
@Testcontainers
class ClickAndVisitConcurrencyIntegrationTest {

    private static final int CONCURRENT_REQUESTS = 12;
    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");
    private static final Instant FIXED_NOW = Instant.parse("2026-11-04T03:00:00Z");

    @Container
    static final MySQLContainer mysql = new MySQLContainer("mysql:8.0.36")
            .withDatabaseName("daejeoniac_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void mysqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class TestClockConfig {
        @Bean
        Clock clock() {
            return Clock.fixed(FIXED_NOW, ZoneOffset.UTC);
        }
    }

    @Autowired private MemberRepository memberRepository;
    @Autowired private PlaceRepository placeRepository;
    @Autowired private PlaceClickLogRepository clickLogRepository;
    @Autowired private VisitedPlaceRepository visitedPlaceRepository;
    @Autowired private PlaceClickLogService clickLogService;
    @Autowired private VisitVerificationService visitVerificationService;

    @Test
    void concurrentClicks_areIdempotent_andStoreExactlyOneRow() throws Exception {
        Member member = newMember();
        Place place = newPlace();

        List<Boolean> outcomes = runConcurrently(CONCURRENT_REQUESTS, () -> {
            clickLogService.saveClickLog(member.getId(), place.getId());
            return true; // INSERT IGNORE는 중복도 API 오류 없이 처리해야 한다.
        });

        assertThat(outcomes).hasSize(CONCURRENT_REQUESTS).containsOnly(true);
        // 같은 회원과 장소를 다른 테스트가 쓰지 않으므로 날짜별 조회는 정확히 1건.
        LocalDate today = LocalDate.now(SEOUL);
        assertThat(clickLogRepository.countByPlace_IdAndClickedAtGreaterThanEqualAndClickedAtLessThan(
                place.getId(), today.atStartOfDay(), today.plusDays(1).atStartOfDay()
        )).isEqualTo(1L);
    }

    @Test
    void concurrentVisits_allowOneSuccess_andRejectOthers_asConflict() throws Exception {
        Member member = newMember();
        // 일반 소비 장소로 생성해 방문 보상 Receipt 생성 경로를 테스트 대상에서 제외한다.
        Place place = newPlace();
        VisitVerificationRequest request = mock(VisitVerificationRequest.class);
        when(request.getLatitude()).thenReturn(36.3504);
        when(request.getLongitude()).thenReturn(127.3845);
        when(request.getAccuracy()).thenReturn(10.0);
        when(request.getMeasuredAt()).thenReturn(FIXED_NOW.minusSeconds(30));

        List<Boolean> outcomes = runConcurrently(CONCURRENT_REQUESTS, () -> {
            try {
                visitVerificationService.verifyVisit(member.getId(), place.getId(), request);
                return true;
            } catch (ConflictException expected) {
                return false; // 중복 요청은 도메인 충돌로 응답해야 한다.
            }
        });

        assertThat(outcomes.stream().filter(Boolean::booleanValue).count()).isEqualTo(1L);
        assertThat(outcomes.stream().filter(result -> !result).count())
                .isEqualTo(CONCURRENT_REQUESTS - 1);
        LocalDate visitedDate = FIXED_NOW.atZone(SEOUL).toLocalDate();
        assertThat(visitedPlaceRepository.findAllByMember_IdAndVisitedDateOrderByVisitedAtDesc(
                member.getId(), visitedDate
        )).hasSize(1);
    }

    private Member newMember() {
        String uid = UUID.randomUUID().toString();
        return memberRepository.saveAndFlush(Member.create(
                "concurrency-" + uid, Member.Role.USER, "password", "tester", "test-" + uid
        ));
    }

    private Place newPlace() {
        return placeRepository.saveAndFlush(Place.builder()
                .placeName("concurrency-test-" + UUID.randomUUID())
                .latitude(36.3504)
                .longitude(127.3845)
                .categorySmall("카페")
                .build());
    }

    /**
     * 모든 작업자가 준비되면 한꺼번에 출발한다.
     * 테스트 메서드 자체가 트랜잭션 밖이므로 각 서비스 호출이 독립 TX이다.
     * 예상하지 못한 예외는 Future.get()에서 테스트 실패로 드러난다.
     */
    private static <T> List<T> runConcurrently(int workers, Callable<T> action) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(workers);
        CountDownLatch ready = new CountDownLatch(workers);
        CountDownLatch start = new CountDownLatch(1);
        try {
            List<Future<T>> futures = new ArrayList<>();
            for (int i = 0; i < workers; i++) {
                futures.add(executor.submit(() -> {
                    ready.countDown();
                    if (!start.await(15, TimeUnit.SECONDS)) {
                        throw new IllegalStateException("동시 실행 시작 신호 시간 초과");
                    }
                    return action.call();
                }));
            }
            assertThat(ready.await(15, TimeUnit.SECONDS)).isTrue();
            start.countDown();
            List<T> results = new ArrayList<>();
            for (Future<T> future : futures) {
                results.add(future.get(30, TimeUnit.SECONDS));
            }
            return results;
        } finally {
            start.countDown();
            executor.shutdownNow();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
