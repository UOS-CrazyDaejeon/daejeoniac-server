package com.daejeongwang.uoscrazydaejeon.repository;

import com.daejeongwang.uoscrazydaejeon.dto.response.CongestionResponse;
import com.daejeongwang.uoscrazydaejeon.entity.Congestion;
import com.daejeongwang.uoscrazydaejeon.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CongestionRepository extends JpaRepository<Congestion, Long> {
    boolean existsByPlaceAndDate(Place place, String date);

    CongestionResponse findByPlaceId(Long placeId);

    Optional<Congestion> findFirstByPlace_IdOrderByDateDesc(Long placeId);
}
