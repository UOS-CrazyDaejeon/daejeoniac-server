package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.entity.Place;
import com.daejeongwang.uoscrazydaejeon.util.DistanceCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PlaceProximityVerifier {
    private final DistanceCalculator distanceCalculator;

    private static final double PROXIMITY_RADIUS_METERS  = 250.0;
    private static final double MAX_LOCATION_ACCURACY_METERS = 100.0;
    private static final long MAX_MEASUREMENT_AGE_MINUTES = 5;

    public void verifyNearPlace(Place place, Double latitude, Double longitude, Double accuracy, LocalDateTime measuredAt) {
        validateMeasurement(measuredAt, accuracy);
        validateCoordinates(latitude, longitude);

        double distance = distanceCalculator.calculateMeters(
                latitude,
                longitude,
                place.getLatitude(),
                place.getLongitude()
        );

        if (distance > PROXIMITY_RADIUS_METERS ) {
            throw new IllegalArgumentException("인증 범위를 벗어났습니다.");
        }
    }

    private void validateMeasurement(LocalDateTime measuredAt, Double accuracy) {
        if (measuredAt == null) {
            throw new IllegalArgumentException("위치 측정 시간이 필요합니다.");
        }
        if (accuracy == null || !Double.isFinite(accuracy) || accuracy <= 0) {
            throw new IllegalArgumentException("올바르지 않은 위치 정확도입니다.");
        }
        if (accuracy > MAX_LOCATION_ACCURACY_METERS) {
            throw new IllegalArgumentException("위치 정확도가 충분하지 않습니다.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (measuredAt.isAfter(now)) {
            throw new IllegalArgumentException("위치 측정 시간이 현재보다 이후일 수 없습니다.");
        }
        if (measuredAt.isBefore(now.minusMinutes(MAX_MEASUREMENT_AGE_MINUTES))) {
            throw new IllegalArgumentException("위치 측정 정보가 너무 오래되었습니다.");
        }
    }

    private void validateCoordinates(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("위치정보가 필요합니다.");
        }

        if (!Double.isFinite(latitude) || !Double.isFinite(longitude)) {
            throw new IllegalArgumentException("올바르지 않은 좌표입니다.");
        }

        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("위도는 -90 이상 90 이하여야 합니다.");
        }

        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("경도는 -180 이상 180 이하여야 합니다.");
        }
    }
}
