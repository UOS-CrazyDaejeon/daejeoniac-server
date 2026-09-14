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

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VisitedPlaceRepository extends JpaRepository<VisitedPlace,Long> {
    boolean existsByMember_IdAndPlace_IdAndVisitedDate(Long memberId, Long placeId, LocalDate visitedDate);
    boolean existsByMemberAndPlaceAndVisitedAtGreaterThanEqualAndVisitedAtLessThan(Member member, Place place, Instant start, Instant end);
    Optional<VisitedPlace> findByIdAndMember_Id(Long visitedPlaceId, Long memberId);

    @EntityGraph(attributePaths = "place")
    List<VisitedPlace> findAllByMember_IdOrderByVisitedAtDesc(Long memberId);

    @EntityGraph(attributePaths = "place")
    List<VisitedPlace> findAllByMember_IdAndVisitedDateOrderByVisitedAtDesc(Long memberId, LocalDate visitedDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select vp
        from VisitedPlace vp
        where vp.id = :visitedPlaceId
          and vp.member.id = :memberId
        """)
    Optional<VisitedPlace> findByIdAndMemberIdForUpdate(
            @Param("visitedPlaceId") Long visitedPlaceId,
            @Param("memberId") Long memberId
    );

    void deleteAllByMember_Id(Long memberId);
}
