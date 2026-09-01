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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class PlacePhotoService {
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

        placeProximityVerifier.verifyNearPlace(
                place,
                request.getLatitude(),
                request.getLongitude(),
                request.getAccuracy(),
                request.getMeasuredAt()
        );

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

    public List<PlacePhotoByPlaceResponse> getPlacePhotosByPlace(Long placeId) {
        if (!placeRepository.existsById(placeId)) {
            throw new ResourceNotFoundException("장소가 없습니다.");
        }

        return placePhotoRepository.findAllByPlace_IdOrderByCreatedAtDesc(placeId)
                .stream()
                .map(placePhoto -> PlacePhotoByPlaceResponse.builder()
                        .placePhotoId(placePhoto.getId())
                        .imageUrl(s3Service.createPublicUrl(placePhoto.getObjectKey()))
                        .createdAt(placePhoto.getCreatedAt())
                        .build())
                .toList();
    }

    public List<PlacePhotoResponse> getMyPlacePhotos(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ResourceNotFoundException("회원이 없습니다.");
        }

        return placePhotoRepository.findAllByMember_IdOrderByCreatedAtDesc(memberId)
                .stream()
                .map(placePhoto -> PlacePhotoResponse.builder()
                        .placePhotoId(placePhoto.getId())
                        .placeId(placePhoto.getPlace().getId())
                        .placeName(placePhoto.getPlace().getPlaceName())
                        .imageUrl(s3Service.createPublicUrl(placePhoto.getObjectKey()))
                        .createdAt(placePhoto.getCreatedAt())
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
