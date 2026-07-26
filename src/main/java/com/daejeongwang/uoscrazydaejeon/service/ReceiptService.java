package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.dto.response.ReceiptStatusResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.ReceiptUploadUrlResponse;
import com.daejeongwang.uoscrazydaejeon.entity.Member;
import com.daejeongwang.uoscrazydaejeon.entity.Place;
import com.daejeongwang.uoscrazydaejeon.entity.Receipt;
import com.daejeongwang.uoscrazydaejeon.repository.MemberRepository;
import com.daejeongwang.uoscrazydaejeon.repository.PlaceRepository;
import com.daejeongwang.uoscrazydaejeon.repository.ReceiptRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ReceiptService {
    private final ReceiptRepository receiptRepository;
    private final PlaceRepository placeRepository;
    private final MemberRepository memberRepository;
    private final RecommendationSessionService recommendationSessionService;
    private final S3Service s3Service;

    public ReceiptUploadUrlResponse issueUploadUrl(Long placeId, Double latitude, Double longitude, String contentType) {
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("회원이 없습니다."));

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("장소가 없습니다."));

        recommendationSessionService.validateRecommendedPlace(member.getId(), placeId);

        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("위치 정보가 필요합니다.");
        }

        double distance = calculateDistance(latitude, longitude, place.getLatitude(), place.getLongitude());
        if(distance > 250) {
            throw new IllegalArgumentException("인증 범위를 벗어났습니다.");
        }

        UUID receiptUuid = UUID.randomUUID();
        String extension = switch(contentType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            default -> throw new IllegalArgumentException("지원하지 않는 이미지 형식입니다.");
        };
        String objectKey = "receipt/" + receiptUuid + "." + extension;

        String uploadUrl = s3Service.createUploadUrl(objectKey, contentType);

        Receipt receipt = Receipt.builder()
                .member(member)
                .place(place)
                .receiptUuid(receiptUuid)
                .objectKey(objectKey)
                .build();

        Receipt savedReceipt = receiptRepository.save(receipt);

        return ReceiptUploadUrlResponse.builder()
                .receiptId(savedReceipt.getReceiptId())
                .uploadUrl(uploadUrl)
                .expiresIn(600)
                .build();
    }

    @Transactional
    public void saveOcrResult(UUID receiptUuid, Receipt.OcrStatus ocrStatus, String ocrPlaceName, LocalDateTime ocrPaidAt) {
        Receipt receipt = receiptRepository.findByReceiptUuid(receiptUuid)
                .orElseThrow(() -> new IllegalArgumentException("영수증 인증 요청이 없습니다."));

        if(receipt.getOcrStatus() != Receipt.OcrStatus.PENDING) { return; }

        if (ocrStatus == Receipt.OcrStatus.PENDING) {
            throw new IllegalArgumentException("OCR 처리중 입니다.");
        }
        if(ocrStatus == Receipt.OcrStatus.FAILED) {
            receipt.ocrFailure();
            return;
        }

        if (ocrPlaceName == null || ocrPaidAt == null) {
            throw new IllegalArgumentException(
                    "OCR 성공 결과에는 장소명과 결제 시간이 필요합니다."
            );
        }

        boolean duplicate = receiptRepository.existsByMember_IdAndPlace_PlaceIdAndOcrPaidAtGreaterThanEqualAndOcrPaidAtLessThanAndVerifyStatus(
                receipt.getMember().getId(),
                receipt.getPlace().getPlaceId(),
                ocrPaidAt.toLocalDate().atStartOfDay(),
                ocrPaidAt.toLocalDate().plusDays(1).atStartOfDay(),
                Receipt.ReceiptStatus.APPROVED
        );
        boolean placeMatched = receipt.getPlace().getPlaceName().equalsIgnoreCase(ocrPlaceName.trim());
        boolean paidToday = ocrPaidAt.toLocalDate().equals(LocalDate.now());
        boolean valid = duplicate && placeMatched && paidToday;

        receipt.ocrSuccess(ocrPlaceName, ocrPaidAt, valid);
    }

    public ReceiptStatusResponse getReceiptStatus(Long receiptId) {
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("회원이 없습니다."));

        Receipt receipt = receiptRepository.findByReceiptIdAndMember_Id(receiptId, member.getId())
                .orElseThrow(() ->  new IllegalArgumentException("영수증을 찾을 수 없습니다."));

        boolean gachaAvailable = receipt.getVerifyStatus() == Receipt.ReceiptStatus.APPROVED;

        return ReceiptStatusResponse.builder()
                .receiptId(receipt.getReceiptId())
                .placeId(receipt.getPlace().getPlaceId())
                .placeName(receipt.getPlace().getPlaceName())
                .verifyStatus(receipt.getVerifyStatus())
                .ocrStatus(receipt.getOcrStatus())
                .gachaAvailable(gachaAvailable)
                .build();

    }


    private double calculateDistance(Double userLat, Double userLon, Double placeLat, Double placeLon) {
        double earthRadius = 6371000;

        double latDistance = Math.toRadians(placeLat - userLat);
        double lonDistance = Math.toRadians(placeLon - userLon);

        double a =
                Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat))
                * Math.cos(Math.toRadians(placeLat))
                * Math.sin(lonDistance / 2)
                * Math.sin(lonDistance / 2);

        double c = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return earthRadius * c;
    }




}
