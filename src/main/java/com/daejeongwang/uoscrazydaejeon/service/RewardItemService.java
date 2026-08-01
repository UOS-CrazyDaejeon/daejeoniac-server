package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.dto.request.RewardItemCreateRequest;
import com.daejeongwang.uoscrazydaejeon.entity.RewardItem;
import com.daejeongwang.uoscrazydaejeon.repository.RewardItemRepsitory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RewardItemService {

    private final RewardItemRepsitory rewardItemRepsitory;

    // 상품 저장
    @Transactional
    public RewardItem createRewardItem(RewardItemCreateRequest request) {
        RewardItem rewardItem = RewardItem.builder()
                .probability(request.getProbability())
                // 현재는 POINT로 고정
                .itemType(RewardItem.RewardItemType.POINT)
                .totalStock(request.getTotalStock())
                .currentStock(request.getTotalStock())
                .build();

        return rewardItemRepsitory.save(rewardItem);

    }

    // 상품 목록 전체 조회
    public List<RewardItem> findAll() {
        return rewardItemRepsitory.findAll();
    }
}
