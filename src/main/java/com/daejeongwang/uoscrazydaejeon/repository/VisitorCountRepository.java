package com.daejeongwang.uoscrazydaejeon.repository;

import com.daejeongwang.uoscrazydaejeon.entity.Place;
import com.daejeongwang.uoscrazydaejeon.entity.VisitorCount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VisitorCountRepository extends JpaRepository<VisitorCount,Long> {
    boolean existsByPlaceAndDate(Place place, LocalDate date);
    List<VisitorCount> findAllByPlace_IdOrderByDateDesc(Long placeId);
    Optional<VisitorCount> findFirstByPlace_IdOrderByDateDesc(Long placeId);
}
