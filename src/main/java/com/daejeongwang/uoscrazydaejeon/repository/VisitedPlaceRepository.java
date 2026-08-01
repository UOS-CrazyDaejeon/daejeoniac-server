package com.daejeongwang.uoscrazydaejeon.repository;

import com.daejeongwang.uoscrazydaejeon.entity.Member;
import com.daejeongwang.uoscrazydaejeon.entity.Place;
import com.daejeongwang.uoscrazydaejeon.entity.VisitedPlace;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VisitedPlaceRepository extends JpaRepository<VisitedPlace,Long> {
    boolean existsByMemberAndPlaceAndVisitedAtGreaterThanEqualAndVisitedAtLessThan(Member member, Place place, LocalDateTime start, LocalDateTime end);
    Optional<VisitedPlace> findByVisitedPlaceIdAndMember_Id(Long visitedPlaceId, Long memberId);

    @EntityGraph(attributePaths = "place")
    List<VisitedPlace> findAllByMember_IdOrderByVisitedAtDesc(Long memberId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select vp
        from VisitedPlace vp
        where vp.visitedPlaceId = :visitedPlaceId
          and vp.member.id = :memberId
        """)
    Optional<VisitedPlace> findByVisitedPlaceIdAndMemberIdForUpdate(
            @Param("visitedPlaceId") Long visitedPlaceId,
            @Param("memberId") Long memberId
    );
}
