package com.daejeongwang.uoscrazydaejeon.repository;

import com.daejeongwang.uoscrazydaejeon.entity.Receipt;
import com.daejeongwang.uoscrazydaejeon.entity.RewardDrawLog;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RewardDrawLogRepository extends JpaRepository<RewardDrawLog, Long> {

    boolean existsByReceipt(Receipt receipt);

    @EntityGraph(attributePaths = {"receipt.visitedPlace.place", "rewardItem"})
    List<RewardDrawLog> findAllByMember_IdOrderByCreatedAtDesc(Long memberId);

    void deleteAllByMember_Id(Long memberId);
}
