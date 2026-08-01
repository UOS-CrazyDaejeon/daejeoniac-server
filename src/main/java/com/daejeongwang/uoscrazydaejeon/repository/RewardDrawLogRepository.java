package com.daejeongwang.uoscrazydaejeon.repository;

import com.daejeongwang.uoscrazydaejeon.entity.Receipt;
import com.daejeongwang.uoscrazydaejeon.entity.RewardDrawLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RewardDrawLogRepository extends JpaRepository<RewardDrawLog, Long> {

    boolean existsByReceipt(Receipt receipt);
}
