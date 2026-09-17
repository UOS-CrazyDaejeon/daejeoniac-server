package com.daejeongwang.uoscrazydaejeon.repository;

import com.daejeongwang.uoscrazydaejeon.entity.Receipt;
import com.daejeongwang.uoscrazydaejeon.entity.VisitRewardDrawLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VisitRewardDrawLogRepository extends JpaRepository<VisitRewardDrawLog, Long> {

    boolean existsByReceipt(Receipt receipt);

    @Query("select log.receipt.id from VisitRewardDrawLog log where log.receipt in :receipts")
    List<Long> findUsedReceiptIdsByReceiptIn(@Param("receipts") List<Receipt> receipts);

    void deleteAllByMember_Id(Long memberId);
}
