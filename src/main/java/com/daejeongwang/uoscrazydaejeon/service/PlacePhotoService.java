package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.dto.request.PlacePhotoUploadUrlRequest;
import com.daejeongwang.uoscrazydaejeon.dto.response.PlacePhotoByPlaceResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.PlacePhotoUploadUrlResponse;
import com.daejeongwang.uoscrazydaejeon.entity.Member;
import com.daejeongwang.uoscrazydaejeon.entity.Place;
import com.daejeongwang.uoscrazydaejeon.entity.PlacePhoto;
import com.daejeongwang.uoscrazydaejeon.exception.ConflictException;
import com.daejeongwang.uoscrazydaejeon.exception.ResourceNotFoundException;
import com.daejeongwang.uoscrazydaejeon.exception.UnsupportedMediaTypeException;
import com.daejeongwang.uoscrazydaejeon.repository.MemberRepository;
import com.daejeongwang.uoscrazydaejeon.repository.PlacePhotoRepository;
import com.daejeongwang.uoscrazydaejeon.repository.PlaceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PlacePhotoService {
    private final PlacePhotoRepository placePhotoRepository;
    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;
    private final S3Service s3Service;

    private static final double VISIT_VERIFICATION_RADIUS_METERS = 250.0;
    private static final double MAX_LOCATION_ACCURACY_METERS = 100.0;
    private static final long MAX_MEASUREMENT_AGE_MINUTES = 5;

    public PlacePhotoUploadUrlResponse issueUploadUrl(Long memberId, Long placeId, PlacePhotoUploadUrlRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("회원이 없습니다."));

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new ResourceNotFoundException("장소가 없습니다."));

        validateMeasurement(request.getMeasuredAt(), request.getAccuracy());
        validateVisitLocation(request.getLatitude(), request.getLongitude(), place
        );
        String contentType = request.getContentType();
        String extension = switch (contentType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            default -> throw new UnsupportedMediaTypeException("지원하지 않는 이미지 형식입니다.");
        };

        UUID photoUuid = UUID.randomUUID();
        String objectKey = "place-photos/" + photoUuid + "." + extension;

        String uploadUrl = s3Service.createUploadUrl(objectKey, contentType);

        PlacePhoto placePhoto = PlacePhoto.builder()
                .member(member)
                .place(place)
                .objectKey(objectKey)
                .build();

        PlacePhoto savedPlacePhoto = placePhotoRepository.save(placePhoto);

        return PlacePhotoUploadUrlResponse.builder()
                .placePhotoId(savedPlacePhoto.getId())
                .uploadUrl(uploadUrl)
                .expiresIn(300)
                .build();
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

    private void validateVisitLocation(Double latitude, Double longitude, Place place) {
        validateCoordinates(latitude, longitude);

        double distance = calculateDistance(
                latitude,
                longitude,
                place.getLatitude(),
                place.getLongitude()
        );

        if (distance > VISIT_VERIFICATION_RADIUS_METERS) {
            throw new IllegalArgumentException("인증 범위를 벗어났습니다.");
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

    private double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        double earthRadius = 6371000;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a =
                Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                        + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2)
                        * Math.sin(lonDistance / 2);
        a = Math.max(0.0, Math.min(1.0, a));

        double c = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return earthRadius * c;
    }

    @Transactional
    public void completeUpload(Long memberId, Long placePhotoId) {
        PlacePhoto placePhoto = placePhotoRepository.findByIdAndMember_Id(placePhotoId, memberId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "장소 사진이 없거나 본인이 등록한 사진이 아닙니다."
                        )
                );

        if (placePhoto.getUploadStatus() == PlacePhoto.UploadStatus.COMPLETED) {
            return;
        }
        if (!s3Service.existsObject(placePhoto.getObjectKey())) {
            throw new ConflictException("S3에 업로드된 사진을 찾을 수 없습니다.");
        }

        placePhoto.complete();
    }


    public List<PlacePhotoByPlaceResponse> getPlacePhotosByPlace(Long placeId) {
        if (!placeRepository.existsById(placeId)) {
            throw new ResourceNotFoundException("장소가 없습니다.");
        }

        return placePhotoRepository.findAllByPlace_IdAndUploadStatusOrderByCreatedAtDesc(placeId, PlacePhoto.UploadStatus.COMPLETED)
                .stream()
                .map(placePhoto -> PlacePhotoByPlaceResponse.builder()
                        .placePhotoId(placePhoto.getId())
                        .imageUrl(s3Service.createPublicUrl(placePhoto.getObjectKey()))
                        .createdAt(placePhoto.getCreatedAt())
                        .build())
                .toList();
    }
}
