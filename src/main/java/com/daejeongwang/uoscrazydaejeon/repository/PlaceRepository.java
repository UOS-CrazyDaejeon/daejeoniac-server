package com.daejeongwang.uoscrazydaejeon.repository;

import com.daejeongwang.uoscrazydaejeon.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    boolean existsByPlaceNameAndPlaceAddressAndCategoryLarge(
            String placeName,
            String placeAddress,
            String categoryLarge
    );

    Optional<Place> findByPlaceName(String placeName);
}
