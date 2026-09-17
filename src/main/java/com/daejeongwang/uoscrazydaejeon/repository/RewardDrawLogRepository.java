package com.daejeongwang.uoscrazydaejeon.repository;

import com.daejeongwang.uoscrazydaejeon.entity.Receipt;
import com.daejeongwang.uoscrazydaejeon.entity.RewardDrawLog;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RewardDrawLogRepository extends JpaRepository<RewardDrawLog, Long> {

    boolean existsByReceipt(Receipt receipt);

    @Query("select log.receipt.id from RewardDrawLog log where log.receipt in :receipts")
    List<Long> findUsedReceiptIdsByReceiptIn(@Param("receipts") List<Receipt> receipts);

    @EntityGraph(attributePaths = {"receipt.visitedPlace.place", "rewardItem", "visitRewardItem"})
    List<RewardDrawLog> findAllByMember_IdOrderByCreatedAtDesc(Long memberId);

    void deleteAllByMember_Id(Long memberId);
}
