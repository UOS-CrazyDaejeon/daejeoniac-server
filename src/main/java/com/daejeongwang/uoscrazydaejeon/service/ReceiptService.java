package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.dto.response.ReceiptUploadUrlResponse;
import com.daejeongwang.uoscrazydaejeon.entity.Member;
import com.daejeongwang.uoscrazydaejeon.entity.Place;
import com.daejeongwang.uoscrazydaejeon.entity.Receipt;
import com.daejeongwang.uoscrazydaejeon.repository.MemberRepository;
import com.daejeongwang.uoscrazydaejeon.repository.PlaceRepository;
import com.daejeongwang.uoscrazydaejeon.repository.ReceiptRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ReceiptService {
    private final ReceiptRepository receiptRepository;
    private final PlaceRepository placeRepository;
    private final MemberRepository memberRepository;
    private final RecommendationSessionService recommendationSessionService;
    private final S3Service s3Service;

    public ReceiptUploadUrlResponse issueUploadUrl(Long placeId, String contentType) {
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("회원이 없습니다."));

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("장소가 없습니다."));

        recommendationSessionService.validateRecommendedPlace(member.getId(), placeId);

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


}
