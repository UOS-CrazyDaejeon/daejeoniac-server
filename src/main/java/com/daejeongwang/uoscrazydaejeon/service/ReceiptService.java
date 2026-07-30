package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.dto.response.ReceiptStatusResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.ReceiptUploadUrlResponse;
import com.daejeongwang.uoscrazydaejeon.entity.Member;
import com.daejeongwang.uoscrazydaejeon.entity.Receipt;
import com.daejeongwang.uoscrazydaejeon.entity.VisitedPlace;
import com.daejeongwang.uoscrazydaejeon.repository.MemberRepository;
import com.daejeongwang.uoscrazydaejeon.repository.ReceiptRepository;
import com.daejeongwang.uoscrazydaejeon.repository.VisitedPlaceRepository;
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
    private final VisitedPlaceRepository visitedPlaceRepository;
    private final MemberRepository memberRepository;
    private final S3Service s3Service;

    @Transactional
    public ReceiptUploadUrlResponse issueUploadUrl(Long visitedPlaceId, String contentType) {
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("회원이 없습니다."));

        VisitedPlace visitedPlace = visitedPlaceRepository.findByVisitedPlaceIdAndMember_Id(visitedPlaceId, member.getId())
                .orElseThrow(() -> new IllegalArgumentException("방문 기록이 없거나 본인의 방문 기록이 아닙니다."));

        boolean visitIsToday = visitedPlace.getVisitedDate().equals(LocalDate.now());

        if (!visitIsToday) {
            throw new IllegalArgumentException("방문 인증 당일에만 영수증을 등록할 수 있습니다.");
        }

        boolean approvedExists = receiptRepository.existsByVisitedPlaceAndVerifyStatus(visitedPlace, Receipt.ReceiptStatus.APPROVED);
        if(approvedExists){
            throw new IllegalArgumentException("이미 승인된 영수증이 있습니다.");
        }
        expirePendingReceipt(visitedPlace);

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
                .visitedPlace(visitedPlace)
                .receiptUuid(receiptUuid)
                .objectKey(objectKey)
                .build();

        Receipt savedReceipt = receiptRepository.save(receipt);

        return ReceiptUploadUrlResponse.builder()
                .receiptId(savedReceipt.getReceiptId())
                .uploadUrl(uploadUrl)
                .expiresIn(300)
                .build();
    }

    @Transactional
    public void saveOcrResult(UUID receiptUuid, Receipt.OcrStatus ocrStatus, String ocrPlaceName, String ocrPlaceAddress, LocalDateTime ocrPaidAt) {
        Receipt receipt = receiptRepository.findByReceiptUuid(receiptUuid)
                .orElseThrow(() -> new IllegalArgumentException("영수증 인증 요청이 없습니다."));

        if (receipt.getVerifyStatus() != Receipt.ReceiptStatus.PENDING) {
            return;
        }

        if (isExpired(receipt)) {
            receipt.expire();
            return;
        }
        if(receipt.getOcrStatus() != Receipt.OcrStatus.PENDING) { return; }

        if (ocrStatus == Receipt.OcrStatus.PENDING) {
            throw new IllegalArgumentException("OCR 처리중 입니다.");
        }
        if(ocrStatus == Receipt.OcrStatus.FAILED) {
            receipt.ocrFailure();
            return;
        }

        if (ocrPlaceName == null || ocrPlaceName.isBlank()
                || ocrPlaceAddress == null || ocrPlaceAddress.isBlank()
                || ocrPaidAt == null) {
            throw new IllegalArgumentException(
                    "OCR 성공 결과에는 장소명, 주소와 결제 시간이 필요합니다."
            );
        }

        //TODO: 주소->좌표 변환 구현 예정
        boolean placeMatched = true;

        boolean paidOnVisitedDate = ocrPaidAt.toLocalDate().isEqual(receipt.getVisitedPlace().getVisitedDate());
        boolean valid = placeMatched && paidOnVisitedDate;

        receipt.ocrSuccess(ocrPlaceName, ocrPlaceAddress, ocrPaidAt, valid);
    }

    public ReceiptStatusResponse getReceiptStatus(Long receiptId) {
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("회원이 없습니다."));

        Receipt receipt = receiptRepository.findByReceiptIdAndMember_Id(receiptId, member.getId())
                .orElseThrow(() ->  new IllegalArgumentException("영수증을 찾을 수 없습니다."));

        boolean gachaAvailable = receipt.getVerifyStatus() == Receipt.ReceiptStatus.APPROVED;

        return ReceiptStatusResponse.builder()
                .receiptId(receipt.getReceiptId())
                .placeId(receipt.getVisitedPlace().getPlace().getPlaceId())
                .placeName(receipt.getVisitedPlace().getPlace().getPlaceName())
                .verifyStatus(receipt.getVerifyStatus())
                .ocrStatus(receipt.getOcrStatus())
                .gachaAvailable(gachaAvailable)
                .build();

    }


    private boolean isExpired(Receipt receipt) {
        return receipt.getVerifyStatus() == Receipt.ReceiptStatus.PENDING
                && receipt.getOcrStatus() == Receipt.OcrStatus.PENDING
                && !receipt.getCreatedAt().plusMinutes(10).isAfter(LocalDateTime.now());
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

        throw new IllegalArgumentException("처리 중인 영수증이 있습니다.");
    }

}
