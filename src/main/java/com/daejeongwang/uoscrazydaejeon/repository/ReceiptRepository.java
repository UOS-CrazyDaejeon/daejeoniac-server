package com.daejeongwang.uoscrazydaejeon.repository;

import com.daejeongwang.uoscrazydaejeon.entity.Receipt;
import com.daejeongwang.uoscrazydaejeon.entity.VisitedPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    Optional<Receipt> findByReceiptUuid(UUID receiptUuid);
    Optional<Receipt> findByReceiptIdAndMember_Id(Long receiptId, Long memberId);
    boolean existsByVisitedPlaceAndVerifyStatus(
            VisitedPlace visitedPlace,
            Receipt.ReceiptStatus verifyStatus
    );
    Optional<Receipt> findFirstByVisitedPlaceAndVerifyStatusOrderByCreatedAtDesc(
            VisitedPlace visitedPlace,
            Receipt.ReceiptStatus verifyStatus
    );
}
