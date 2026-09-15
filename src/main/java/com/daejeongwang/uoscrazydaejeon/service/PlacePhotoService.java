package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.client.AiServerClient;
import com.daejeongwang.uoscrazydaejeon.dto.request.PlacePhotoUploadRequest;
import com.daejeongwang.uoscrazydaejeon.dto.response.PlacePhotoByPlaceResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.PlacePhotoResponse;
import com.daejeongwang.uoscrazydaejeon.entity.Member;
import com.daejeongwang.uoscrazydaejeon.entity.Place;
import com.daejeongwang.uoscrazydaejeon.entity.PlacePhoto;
import com.daejeongwang.uoscrazydaejeon.exception.ResourceNotFoundException;
import com.daejeongwang.uoscrazydaejeon.exception.UnsupportedMediaTypeException;
import com.daejeongwang.uoscrazydaejeon.repository.MemberRepository;
import com.daejeongwang.uoscrazydaejeon.repository.PlacePhotoRepository;
import com.daejeongwang.uoscrazydaejeon.repository.PlaceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class PlacePhotoService {
    private static final long GPS_VERIFICATION_BYPASS_MEMBER_ID_1 = 3L;
    private static final long GPS_VERIFICATION_BYPASS_MEMBER_ID_2 = 6L;

    private final PlacePhotoRepository placePhotoRepository;
    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;
    private final S3Service s3Service;
    private final PlaceProximityVerifier placeProximityVerifier;
    private final AiServerClient aiServerClient;

    public PlacePhotoResponse uploadPlacePhoto(Long memberId, Long placeId, MultipartFile image, PlacePhotoUploadRequest request){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("회원이 없습니다."));

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new ResourceNotFoundException("장소가 없습니다."));

        if (member.getId() != GPS_VERIFICATION_BYPASS_MEMBER_ID_1
                && member.getId() != GPS_VERIFICATION_BYPASS_MEMBER_ID_2) {
            placeProximityVerifier.verifyNearPlace(
                    place,
                    request.getLatitude(),
                    request.getLongitude(),
                    request.getAccuracy(),
                    request.getMeasuredAt()
            );
        }

        String contentType = image.getContentType();
        if (!List.of("image/jpeg", "image/png").contains(contentType)) {
            throw new UnsupportedMediaTypeException(
                    "지원하지 않는 이미지 형식입니다."
            );
        }

        byte[] mosaicImage = aiServerClient.requestFaceMosaic(image);

        UUID photoUuid = UUID.randomUUID();
        String objectKey = "place-photos/" + photoUuid + ".jpg";

        s3Service.uploadImage(objectKey, mosaicImage, "image/jpeg");

        PlacePhoto placePhoto = PlacePhoto.builder()
                .member(member)
                .place(place)
                .objectKey(objectKey)
                .build();

        try {
            PlacePhoto savedPlacePhoto = placePhotoRepository.save(placePhoto);

            return PlacePhotoResponse.builder()
                    .placePhotoId(savedPlacePhoto.getId())
                    .placeId(place.getId())
                    .placeName(place.getPlaceName())
                    .imageUrl(s3Service.createPublicUrl(objectKey))
                    .createdAt(savedPlacePhoto.getCreatedAt())
                    .build();
        } catch (Exception e) {
            try {
                s3Service.deleteObject(objectKey);
            } catch (Exception deleteException) {
                log.error("S3 보상 삭제 실패. objectKey={}", objectKey, deleteException);
            }

            throw e;
        }
    }

    public PlacePhotoResponse updatePlacePhoto(Long memberId, Long placePhotoId, MultipartFile image, PlacePhotoUploadRequest request) {
        PlacePhoto placePhoto = placePhotoRepository.findByIdAndMember_Id(placePhotoId, memberId)
                .orElseThrow(() -> new ResourceNotFoundException("장소 사진이 없거나 본인이 등록한 사진이 아닙니다."));

        if (memberId != GPS_VERIFICATION_BYPASS_MEMBER_ID_1
                && memberId != GPS_VERIFICATION_BYPASS_MEMBER_ID_2) {
            placeProximityVerifier.verifyNearPlace(
                    placePhoto.getPlace(),
                    request.getLatitude(),
                    request.getLongitude(),
                    request.getAccuracy(),
                    request.getMeasuredAt()
            );
        }

        String contentType = image.getContentType();
        if (!List.of("image/jpeg", "image/png").contains(contentType)) {
            throw new UnsupportedMediaTypeException(
                    "지원하지 않는 이미지 형식입니다."
            );
        }

        byte[] mosaicImage = aiServerClient.requestFaceMosaic(image);

        UUID photoUuid = UUID.randomUUID();
        String objectKey = "place-photos/" + photoUuid + ".jpg";
        String oldObjectKey = placePhoto.getObjectKey();

        s3Service.uploadImage(objectKey, mosaicImage, "image/jpeg");

        PlacePhoto savedPlacePhoto;
        try {
            placePhoto.updateObjectKey(objectKey);
            savedPlacePhoto = placePhotoRepository.save(placePhoto);
        } catch (Exception e) {
            try {
                s3Service.deleteObject(objectKey);
            } catch (Exception deleteException) {
                log.error("S3 보상 삭제 실패. objectKey={}", objectKey, deleteException);
            }

            throw e;
        }

        try {
            s3Service.deleteObject(oldObjectKey);
        } catch (Exception deleteException) {
            log.error("기존 S3 장소 사진 삭제 실패. objectKey={}", oldObjectKey, deleteException);
        }

        return PlacePhotoResponse.builder()
                .placePhotoId(savedPlacePhoto.getId())
                .placeId(savedPlacePhoto.getPlace().getId())
                .placeName(savedPlacePhoto.getPlace().getPlaceName())
                .imageUrl(s3Service.createPublicUrl(objectKey))
                .createdAt(savedPlacePhoto.getCreatedAt())
                .build();
    }

    public List<PlacePhotoByPlaceResponse> getPlacePhotosByPlace(
            Long memberId,
            Long placeId,
            int page,
            int size
    ) {
        if (!placeRepository.existsById(placeId)) {
            throw new ResourceNotFoundException("장소가 없습니다.");
        }

        Pageable pageable = PageRequest.of(page, size);

        return placePhotoRepository.findAllByPlace_IdOrderByCreatedAtDesc(placeId, pageable)
                .stream()
                .map(placePhoto -> PlacePhotoByPlaceResponse.builder()
                        .placePhotoId(placePhoto.getId())
                        .imageUrl(s3Service.createPublicUrl(placePhoto.getObjectKey()))
                        .createdAt(placePhoto.getCreatedAt())
                        .me(placePhoto.getMember().getId().equals(memberId))
                        .build())
                .toList();
    }

    public void deletePlacePhoto(Long memberId, Long placePhotoId) {
        PlacePhoto placePhoto = placePhotoRepository.findByIdAndMember_Id(placePhotoId, memberId)
                .orElseThrow(() -> new ResourceNotFoundException( "장소 사진이 없거나 본인이 등록한 사진이 아닙니다."));

        s3Service.deleteObject(placePhoto.getObjectKey());
        placePhotoRepository.delete(placePhoto);
    }
}
