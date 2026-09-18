package com.daejeongwang.uoscrazydaejeon.repository;

import com.daejeongwang.uoscrazydaejeon.entity.Place;
import com.daejeongwang.uoscrazydaejeon.entity.PlaceClickLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PlaceClickLogRepository extends JpaRepository<PlaceClickLog, Long> {
    interface PlaceClickCount {
        Long getPlaceId();
        Long getViewerCount();
    }

    long countByPlace_IdAndClickedAtGreaterThanEqualAndClickedAtLessThan(
            Long placeId,
            LocalDateTime startOfDay,
            LocalDateTime startOfNextDay
    );

    boolean existsByMember_IdAndPlace_IdAndClickedAtGreaterThanEqualAndClickedAtLessThan(
            Long memberId,
            Long placeId,
            LocalDateTime startOfDay,
            LocalDateTime startOfNextDay
    );

    @Modifying
    @Query(value = """
            INSERT IGNORE INTO place_click_log (member_id, place_id, clicked_at)
            VALUES (:memberId, :placeId, :clickedAt)
            """, nativeQuery = true)
    int insertIgnore(
            @Param("memberId") Long memberId,
            @Param("placeId") Long placeId,
            @Param("clickedAt") LocalDateTime clickedAt
    );

    @Query("""
            select clickLog.place.id as placeId, count(clickLog.id) as viewerCount
            from PlaceClickLog clickLog
            where clickLog.place.id in :placeIds
              and clickLog.clickedAt >= :startOfDay
              and clickLog.clickedAt < :startOfNextDay
            group by clickLog.place.id
            """)
    List<PlaceClickCount> countByPlaceIdsAndClickedAtBetween(
            @Param("placeIds") List<Long> placeIds,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("startOfNextDay") LocalDateTime startOfNextDay
    );

    @Modifying
    @Query("update PlaceClickLog clickLog set clickLog.member = null where clickLog.member.id = :memberId")
    void detachMember(@Param("memberId") Long memberId);
}
