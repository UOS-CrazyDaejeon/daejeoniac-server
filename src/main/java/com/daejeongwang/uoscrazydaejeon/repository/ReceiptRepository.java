package com.daejeongwang.uoscrazydaejeon.repository;

import com.daejeongwang.uoscrazydaejeon.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    Optional<Receipt> findByReceiptUuid(UUID receiptUuid);
    Optional<Receipt> findByReceiptIdAndMemberId(Long receiptId, Long memberId);
}
