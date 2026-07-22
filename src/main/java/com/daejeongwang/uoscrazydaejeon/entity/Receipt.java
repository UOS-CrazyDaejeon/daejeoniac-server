package com.daejeongwang.uoscrazydaejeon.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(
        uniqueConstraints = @UniqueConstraint(
            name = "uk_receipt_member_place_receiptDate",
            columnNames = {"member_id", "place_id", "receipt_date"}
        )
)
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

    @Column(nullable = false)
    private String receiptImageKey;

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
