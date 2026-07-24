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
    }

    public enum OcrStatus {
        PENDING,
        SUCCESS,
        FAILED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long receiptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID receiptUuid;

    @Column(nullable = false)
    private String objectKey;

    private LocalDateTime createdAt;

    private LocalDateTime verifiedAt;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private ReceiptStatus status = ReceiptStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private OcrStatus ocrStatus = OcrStatus.PENDING;

    private String ocrPlaceName;

    private LocalDateTime ocrPaidAt;


    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public void completeOcr(
            String ocrPlaceName,
            LocalDateTime ocrPaidAt
    ) {
        this.ocrStatus = OcrStatus.SUCCESS;
        this.ocrPlaceName = ocrPlaceName;
        this.ocrPaidAt = ocrPaidAt;
    }

    public void failOcr() {
        this.ocrStatus = OcrStatus.FAILED;
    }

    public void approve() {
        this.status = ReceiptStatus.APPROVED;
        this.verifiedAt = LocalDateTime.now();
    }

    public void reject() {
        this.status = ReceiptStatus.REJECTED;
        this.verifiedAt = LocalDateTime.now();
    }
}
