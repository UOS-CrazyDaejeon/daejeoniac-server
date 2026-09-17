package com.daejeongwang.uoscrazydaejeon.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Receipt {

    public enum ReceiptStatus {
        PENDING,
        APPROVED,
        REJECTED,
        EXPIRED
    }

    public enum OcrStatus {
        PENDING,
        SUCCESS,
        FAILED
    }

    public enum VerificationType {
        VISIT,
        RECEIPT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visited_place_id", nullable = false)
    private VisitedPlace visitedPlace;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID receiptUuid;

    @Column(nullable = false)
    private String objectKey;

    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    private Instant verifiedAt;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private ReceiptStatus verifyStatus = ReceiptStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private OcrStatus ocrStatus = OcrStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, columnDefinition = "varchar(20) default 'RECEIPT'")
    private VerificationType verificationType = VerificationType.RECEIPT;

    private String ocrPlaceName;

    private String ocrPlaceAddress;

    private Instant ocrPaidAt;

    public static Receipt createVisitReceipt(VisitedPlace visitedPlace, Instant now) {
        UUID receiptUuid = UUID.randomUUID();

        return Receipt.builder()
                .visitedPlace(visitedPlace)
                .receiptUuid(receiptUuid)
                .objectKey("receipt/visit-verification/" + receiptUuid)
                .requestedAt(now)
                .verifiedAt(now)
                .verifyStatus(ReceiptStatus.APPROVED)
                .ocrStatus(OcrStatus.SUCCESS)
                .verificationType(VerificationType.VISIT)
                .build();
    }

    public void ocrSuccess(
            String ocrPlaceName,
            String ocrPlaceAddress,
            Instant ocrPaidAt,
            boolean approved,
            Instant now
    ) {
        this.ocrStatus = OcrStatus.SUCCESS;
        this.ocrPlaceName = ocrPlaceName;
        this.ocrPlaceAddress = ocrPlaceAddress;
        this.ocrPaidAt = ocrPaidAt;
        this.verifyStatus = approved ? ReceiptStatus.APPROVED : ReceiptStatus.REJECTED;
        this.verifiedAt = now;
    }

    public void ocrFailure(Instant now) {
        this.ocrStatus = OcrStatus.FAILED;
        this.verifyStatus = ReceiptStatus.REJECTED;
        this.verifiedAt = now;
    }

    public void expire(Instant now) {
        this.verifyStatus = ReceiptStatus.EXPIRED;
        this.verifiedAt = now;
    }

    public void prepareForRetry(Instant now) {
        if (this.verifyStatus != ReceiptStatus.REJECTED) {
            throw new IllegalStateException("거절된 영수증만 재인증할 수 있습니다.");
        }

        this.requestedAt = now;
        this.verifyStatus = ReceiptStatus.PENDING;
        this.ocrStatus = OcrStatus.PENDING;
        this.ocrPlaceName = null;
        this.ocrPlaceAddress = null;
        this.ocrPaidAt = null;
        this.verifiedAt = null;
    }

}
