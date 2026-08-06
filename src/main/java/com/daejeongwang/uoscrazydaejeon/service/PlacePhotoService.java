package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.dto.request.PlacePhotoUploadUrlRequest;
import com.daejeongwang.uoscrazydaejeon.dto.response.PlacePhotoByPlaceResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.PlacePhotoResponse;
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

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PlacePhotoService {
    private final PlacePhotoRepository placePhotoRepository;
    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;
    private final S3Service s3Service;
    private final PlaceProximityVerifier placeProximityVerifier;

    public PlacePhotoUploadUrlResponse issueUploadUrl(Long memberId, Long placeId, PlacePhotoUploadUrlRequest request) {
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

    public List<PlacePhotoResponse> getMyPlacePhotos(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ResourceNotFoundException("회원이 없습니다.");
        }

        return placePhotoRepository.findAllByMember_IdAndUploadStatusOrderByCreatedAtDesc(memberId, PlacePhoto.UploadStatus.COMPLETED)
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
