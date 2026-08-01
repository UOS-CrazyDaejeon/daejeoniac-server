package com.daejeongwang.uoscrazydaejeon.repository;

import com.daejeongwang.uoscrazydaejeon.entity.RewardItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RewardItemRepository extends JpaRepository<RewardItem, Long> {

    List<RewardItem> findByCurrentStockGreaterThan(int num);
}
