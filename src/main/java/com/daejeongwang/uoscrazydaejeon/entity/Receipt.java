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
    private ReceiptStatus verifyStatus = ReceiptStatus.PENDING;

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

    public void ocrSuccess(
            String ocrPlaceName,
            LocalDateTime ocrPaidAt,
            boolean approved
    ) {
        this.ocrStatus = OcrStatus.SUCCESS;
        this.ocrPlaceName = ocrPlaceName;
        this.ocrPaidAt = ocrPaidAt;
        this.verifyStatus = approved ? ReceiptStatus.APPROVED : ReceiptStatus.REJECTED;
        this.verifiedAt = LocalDateTime.now();
    }

    public void ocrFailure() {
        this.ocrStatus = OcrStatus.FAILED;
        this.verifyStatus = ReceiptStatus.REJECTED;
        this.verifiedAt = LocalDateTime.now();
    }

}
