package com.daejeongwang.uoscrazydaejeon.repository;

import com.daejeongwang.uoscrazydaejeon.entity.Receipt;
import com.daejeongwang.uoscrazydaejeon.entity.RewardDrawLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RewardDrawLogRepository extends JpaRepository<RewardDrawLog, Long> {

    boolean existsByReceipt(Receipt receipt);

    List<RewardDrawLog> findAllByMember_IdOrderByCreatedAtDesc(Long memberId);
}
