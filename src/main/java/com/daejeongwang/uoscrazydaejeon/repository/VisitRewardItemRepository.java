package com.daejeongwang.uoscrazydaejeon.repository;

import com.daejeongwang.uoscrazydaejeon.entity.VisitRewardItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitRewardItemRepository extends JpaRepository<VisitRewardItem, Long> {

    List<VisitRewardItem> findByCurrentStockGreaterThanOrCurrentStockIsNull(int stock);
}
