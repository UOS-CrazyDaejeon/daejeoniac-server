package com.daejeongwang.uoscrazydaejeon.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime verifiedAt;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private ReceiptStatus verifyStatus = ReceiptStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private OcrStatus ocrStatus = OcrStatus.PENDING;

    private String ocrPlaceName;

    private String ocrPlaceAddress;

    private LocalDateTime ocrPaidAt;


    @PrePersist
    private void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public void ocrSuccess(
            String ocrPlaceName,
            String ocrPlaceAddress,
            LocalDateTime ocrPaidAt,
            boolean approved
    ) {
        this.ocrStatus = OcrStatus.SUCCESS;
        this.ocrPlaceName = ocrPlaceName;
        this.ocrPlaceAddress = ocrPlaceAddress;
        this.ocrPaidAt = ocrPaidAt;
        this.verifyStatus = approved ? ReceiptStatus.APPROVED : ReceiptStatus.REJECTED;
        this.verifiedAt = LocalDateTime.now();
    }

    public void ocrFailure() {
        this.ocrStatus = OcrStatus.FAILED;
        this.verifyStatus = ReceiptStatus.REJECTED;
        this.verifiedAt = LocalDateTime.now();
    }

    public void expire() {
        this.verifyStatus = ReceiptStatus.EXPIRED;
        this.verifiedAt = LocalDateTime.now();
    }

}
