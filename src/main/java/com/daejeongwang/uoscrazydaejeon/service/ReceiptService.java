package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.client.AddressApiClient;
import com.daejeongwang.uoscrazydaejeon.client.AiServerClient;
import com.daejeongwang.uoscrazydaejeon.dto.response.api.ReceiptOcrResultResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.ReceiptResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.ReceiptStatusResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.ReceiptUploadUrlResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.api.AddressApiResponse;
import com.daejeongwang.uoscrazydaejeon.entity.Member;
import com.daejeongwang.uoscrazydaejeon.entity.Place;
import com.daejeongwang.uoscrazydaejeon.entity.Receipt;
import com.daejeongwang.uoscrazydaejeon.entity.VisitedPlace;
import com.daejeongwang.uoscrazydaejeon.exception.ConflictException;
import com.daejeongwang.uoscrazydaejeon.exception.ResourceNotFoundException;
import com.daejeongwang.uoscrazydaejeon.exception.UnsupportedMediaTypeException;
import com.daejeongwang.uoscrazydaejeon.repository.MemberRepository;
import com.daejeongwang.uoscrazydaejeon.repository.ReceiptRepository;
import com.daejeongwang.uoscrazydaejeon.repository.VisitedPlaceRepository;
import com.daejeongwang.uoscrazydaejeon.util.DistanceCalculator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ReceiptService {
    private final ReceiptRepository receiptRepository;
    private final VisitedPlaceRepository visitedPlaceRepository;
    private final MemberRepository memberRepository;
    private final S3Service s3Service;
    private final AddressApiClient addressApiClient;
    private final DistanceCalculator distanceCalculator;
    private final AiServerClient aiServerClient;

    private static final Duration PENDING_VALID_DURATION = Duration.ofMinutes(5);

    @Transactional
    public ReceiptUploadUrlResponse issueUploadUrl(Long memberId, Long visitedPlaceId, String contentType) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("회원이 없습니다."));

        VisitedPlace visitedPlace = visitedPlaceRepository.findByIdAndMemberIdForUpdate(visitedPlaceId, member.getId())
                .orElseThrow(() -> new ResourceNotFoundException("방문 기록이 없거나 본인의 방문 기록이 아닙니다."));

        boolean visitIsToday = visitedPlace.getVisitedDate().equals(LocalDate.now());

        if (!visitIsToday) {
            throw new ConflictException("방문 인증 당일에만 영수증을 등록할 수 있습니다.");
        }

        boolean approvedExists = receiptRepository.existsByVisitedPlaceAndVerifyStatus(visitedPlace, Receipt.ReceiptStatus.APPROVED);
        if(approvedExists){
            throw new ConflictException("이미 승인된 영수증이 있습니다.");
        }
        expirePendingReceipt(visitedPlace);

        UUID receiptUuid = UUID.randomUUID();
        String extension = switch(contentType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            default -> throw new UnsupportedMediaTypeException("지원하지 않는 이미지 형식입니다.");
        };
        String objectKey = "receipt/" + receiptUuid + "." + extension;

        String uploadUrl = s3Service.createUploadUrl(objectKey, contentType);

        Receipt receipt = Receipt.builder()
                .visitedPlace(visitedPlace)
                .receiptUuid(receiptUuid)
                .objectKey(objectKey)
                .build();

        Receipt savedReceipt = receiptRepository.save(receipt);

        return ReceiptUploadUrlResponse.builder()
                .receiptId(savedReceipt.getId())
                .uploadUrl(uploadUrl)
                .expiresIn(300)
                .build();
    }

    public ReceiptStatusResponse getReceiptStatus(Long memberId, Long receiptId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("회원이 없습니다."));

        Receipt receipt = receiptRepository.findByIdAndVisitedPlace_Member_Id(receiptId, member.getId())
                .orElseThrow(() ->  new ResourceNotFoundException("영수증을 찾을 수 없습니다."));

        boolean gachaAvailable = receipt.getVerifyStatus() == Receipt.ReceiptStatus.APPROVED;

        return ReceiptStatusResponse.builder()
                .receiptId(receipt.getId())
                .placeId(receipt.getVisitedPlace().getPlace().getId())
                .placeName(receipt.getVisitedPlace().getPlace().getPlaceName())
                .verifyStatus(receipt.getVerifyStatus())
                .ocrStatus(receipt.getOcrStatus())
                .gachaAvailable(gachaAvailable)
                .build();

    }

    // 내 영수증 조회
    public List<ReceiptResponse> getMyReceipts(Long memberId) {
        return receiptRepository.findAllByVisitedPlace_Member_IdOrderByCreatedAtDesc(memberId)
                .stream()
                .map(ReceiptResponse::from)
                .toList();
    }

    @Transactional(noRollbackFor = ConflictException.class)
    public ReceiptStatusResponse processOcr(Long memberId, Long receiptId) {
        Receipt receipt = receiptRepository.findByIdAndVisitedPlace_Member_Id(receiptId, memberId)
                .orElseThrow(() -> new ResourceNotFoundException("영수증을 찾을 수 없습니다."));

        if(receipt.getVerifyStatus() != Receipt.ReceiptStatus.PENDING) {
            throw new ConflictException("이미 처리가 완료된 영수증입니다.");
        }

        if (isExpired(receipt)) {
            receipt.expire();
            throw new ConflictException("영수증 인증 요청이 만료되었습니다.");
        }

        if(receipt.getOcrStatus() != Receipt.OcrStatus.PENDING) {
            throw new ConflictException("이미 OCR 처리가 완료된 영수증입니다.");
        }

        if (!s3Service.existsObject(receipt.getObjectKey())) {
            throw new ConflictException("영수증 이미지 업로드가 완료되지 않았습니다.");
        }

        ReceiptOcrResultResponse result = aiServerClient.requestOcr(
                receipt.getReceiptUuid(),
                receipt.getObjectKey()
        );
        if (result == null) {
            throw new IllegalStateException("AI 서버에서 OCR 결과를 반환하지 않았습니다.");
        }
        if (!receipt.getReceiptUuid().equals(result.getReceiptUuid())) {
            throw new IllegalStateException("OCR 결과의 영수증 UUID가 일치하지 않습니다.");
        }

        applyOcrResult(receipt, result);

        return ReceiptStatusResponse.builder()
                .receiptId(receipt.getId())
                .placeId(receipt.getVisitedPlace().getPlace().getId())
                .placeName(receipt.getVisitedPlace().getPlace().getPlaceName())
                .verifyStatus(receipt.getVerifyStatus())
                .ocrStatus(receipt.getOcrStatus())
                .gachaAvailable(
                        receipt.getVerifyStatus() == Receipt.ReceiptStatus.APPROVED
                )
                .build();
    }

    private void applyOcrResult(Receipt receipt, ReceiptOcrResultResponse result) {
        if (receipt.getVerifyStatus() != Receipt.ReceiptStatus.PENDING) {
            return;
        }

        if (isExpired(receipt)) {
            receipt.expire();
            return;
        }
        if(receipt.getOcrStatus() != Receipt.OcrStatus.PENDING) { return; }

        if (result.getOcrStatus() == Receipt.OcrStatus.PENDING) {
            throw new IllegalArgumentException("완료되지 않은 OCR 상태입니다.");
        }
        if(result.getOcrStatus() == Receipt.OcrStatus.FAILED) {
            receipt.ocrFailure();
            return;
        }
        if (result.getOcrStatus() != Receipt.OcrStatus.SUCCESS) {
            throw new IllegalArgumentException("올바르지 않은 OCR 상태입니다.");
        }

        if (result.getOcrPlaceName() == null || result.getOcrPlaceName().isBlank()
                || result.getOcrPlaceAddress() == null || result.getOcrPlaceAddress().isBlank()
                || result.getOcrPaidAt() == null) {
            throw new IllegalArgumentException(
                    "OCR 성공 결과에는 장소명, 주소와 결제 시간이 필요합니다."
            );
        }

        Place place = receipt.getVisitedPlace().getPlace();

        AddressApiResponse addressResponse = addressApiClient.searchCoordinateByAddress(result.getOcrPlaceAddress());

        boolean placeMatched = false;
        if(addressResponse != null && addressResponse.getDocuments() != null && !addressResponse.getDocuments().isEmpty()){
            AddressApiResponse.Document document = addressResponse.getDocuments().get(0);
            double receiptLongitude = Double.parseDouble(document.getLongitude());
            double receiptLatitude = Double.parseDouble(document.getLatitude());

            double distance = distanceCalculator.calculateMeters(
                    place.getLatitude(),
                    place.getLongitude(),
                    receiptLatitude,
                    receiptLongitude
            );

            placeMatched = distance <= 100.0;
        }

        boolean paidOnVisitedDate = result.getOcrPaidAt().toLocalDate().isEqual(receipt.getVisitedPlace().getVisitedDate());
        boolean valid = placeMatched && paidOnVisitedDate;

        receipt.ocrSuccess(result.getOcrPlaceName(), result.getOcrPlaceAddress(), result.getOcrPaidAt(), valid);
    }

    private boolean isExpired(Receipt receipt) {
        return receipt.getVerifyStatus() == Receipt.ReceiptStatus.PENDING
                && receipt.getOcrStatus() == Receipt.OcrStatus.PENDING
                && !receipt.getCreatedAt().plus(PENDING_VALID_DURATION).isAfter(LocalDateTime.now());
    }

    private void expirePendingReceipt(VisitedPlace visitedPlace) {
        Receipt pendingReceipt = receiptRepository
                .findFirstByVisitedPlaceAndVerifyStatusOrderByCreatedAtDesc(
                        visitedPlace,
                        Receipt.ReceiptStatus.PENDING
                )
                .orElse(null);

        if (pendingReceipt == null) {return;}

        if (isExpired(pendingReceipt)) {
            pendingReceipt.expire();
            return;
        }

        throw new ConflictException("처리 중인 영수증이 있습니다.");
    }

}
