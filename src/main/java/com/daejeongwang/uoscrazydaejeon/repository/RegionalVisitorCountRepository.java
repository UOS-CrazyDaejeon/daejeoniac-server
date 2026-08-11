package com.daejeongwang.uoscrazydaejeon.repository;

import com.daejeongwang.uoscrazydaejeon.entity.RegionalVisitorCount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RegionalVisitorCountRepository extends JpaRepository<RegionalVisitorCount,Long> {
    Optional<RegionalVisitorCount> findByDateAndSignguCode(LocalDate date, String signguCode);
    Optional<RegionalVisitorCount> findTopByOrderByDateDesc();
    List<RegionalVisitorCount> findAllByDateOrderBySignguCodeAsc(LocalDate date);
}
