package com.daejeongwang.uoscrazydaejeon.repository;

import com.daejeongwang.uoscrazydaejeon.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    boolean existsByPlaceNameAndPlaceAddressAndCategoryLarge(
            String placneName,
            String placeAddress,
            String categoryLarge
    );
}
