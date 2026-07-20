package com.daejeongwang.uoscrazydaejeon.repository;

import com.daejeongwang.uoscrazydaejeon.entity.Place;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    boolean existsByPlaceNameAndPlaceAddressAndCategoryLarge(String placeName, String placeAddress, String categoryLarge);

    Page<Place> findAll(Pageable pageable);

    Optional<Place> findByPlaceId(Long placeId);

    Optional<Place> findByPlaceName(String placeName);

    @Query(value = """
        SELECT *
        FROM place p
        WHERE p.place_id <> :placeId
          AND p.latitude IS NOT NULL
          AND p.longitude IS NOT NULL
          AND ST_Distance_Sphere(
                POINT(p.longitude, p.latitude),
                POINT(:longitude, :latitude)
              ) <= :radius
        ORDER BY ST_Distance_Sphere(
                POINT(p.longitude, p.latitude),
                POINT(:longitude, :latitude)
              )
        """, nativeQuery = true)
    Page<Place> findNearbyPlaces(
            @Param("placeId") Long placeId,
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radius") Double radius,
            Pageable pageable
    );
}
