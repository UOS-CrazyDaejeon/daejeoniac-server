package com.daejeongwang.uoscrazydaejeon.repository;

import com.daejeongwang.uoscrazydaejeon.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    Optional<Receipt> findByReceiptUuid(UUID receiptUuid);
    Optional<Receipt> findByReceiptIdAndMember_Id(Long receiptId, Long memberId);
    boolean existsByMember_IdAndPlace_PlaceIdAndOcrPaidAtGreaterThanEqualAndOcrPaidAtLessThanAndVerifyStatus(
            Long memberId,
            Long placeId,
            LocalDateTime start,
            LocalDateTime end,
            Receipt.ReceiptStatus verifyStatus
    );
}
