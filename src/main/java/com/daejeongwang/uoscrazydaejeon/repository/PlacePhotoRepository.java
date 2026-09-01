package com.daejeongwang.uoscrazydaejeon.repository;

import com.daejeongwang.uoscrazydaejeon.entity.PlacePhoto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlacePhotoRepository extends JpaRepository<PlacePhoto, Long> {
    List<PlacePhoto> findAllByPlace_IdOrderByCreatedAtDesc(Long placeId);

    Optional<PlacePhoto> findByIdAndMember_Id(Long placePhotoId, Long memberId);

    @EntityGraph(attributePaths = "place")
    List<PlacePhoto> findAllByMember_IdOrderByCreatedAtDesc(Long memberId);
}