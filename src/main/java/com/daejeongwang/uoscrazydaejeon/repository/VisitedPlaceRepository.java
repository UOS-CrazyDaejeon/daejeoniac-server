package com.daejeongwang.uoscrazydaejeon.repository;

import com.daejeongwang.uoscrazydaejeon.entity.Member;
import com.daejeongwang.uoscrazydaejeon.entity.Place;
import com.daejeongwang.uoscrazydaejeon.entity.VisitedPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VisitedPlaceRepository extends JpaRepository<VisitedPlace,Long> {
    boolean existsByMemberAndPlaceAndVisitedAtGreaterThanEqualAndVisitedAtLessThan(Member member, Place place, LocalDateTime start, LocalDateTime end);

    Optional<VisitedPlace> findByVisitedPlaceIdAndMember_Id(Long visitedPlaceId, Long memberId);
}
