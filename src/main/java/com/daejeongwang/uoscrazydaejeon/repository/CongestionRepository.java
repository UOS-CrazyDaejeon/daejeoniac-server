package com.daejeongwang.uoscrazydaejeon.repository;

import com.daejeongwang.uoscrazydaejeon.entity.Congestion;
import com.daejeongwang.uoscrazydaejeon.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CongestionRepository extends JpaRepository<Congestion, Long> {
    boolean existsByPlaceAndDate(Place place, String date);
}
