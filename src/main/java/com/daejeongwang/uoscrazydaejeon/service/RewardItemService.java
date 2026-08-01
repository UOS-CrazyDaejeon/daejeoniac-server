package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.dto.request.RewardItemCreateRequest;
import com.daejeongwang.uoscrazydaejeon.dto.request.RewardItemUpdateRequest;
import com.daejeongwang.uoscrazydaejeon.entity.RewardItem;
import com.daejeongwang.uoscrazydaejeon.exception.ResourceNotFoundException;
import com.daejeongwang.uoscrazydaejeon.repository.RewardItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RewardItemService {

    private final RewardItemRepository rewardItemRepsitory;

    // 상품 저장
    @Transactional
    public RewardItem createRewardItem(RewardItemCreateRequest request) {
        RewardItem rewardItem = RewardItem.builder()
                .probability(request.prbability())
                // 현재는 POINT로 고정
                .itemType(RewardItem.RewardItemType.POINT)
                .rewardValue(request.rewardValue())
                .totalStock(request.totalStock())
                .currentStock(request.totalStock())
                .build();

        return rewardItemRepsitory.save(rewardItem);

    }

    // 상품 정보 수정
    @Transactional
    public RewardItem updateRewardItem(Long rewardId, RewardItemUpdateRequest request) {
        RewardItem rewardItem = rewardItemRepsitory.findById(rewardId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 상품이 존재하지 않습니다."));

        rewardItem.updateRewardItem(
                request.probability(),
                request.rewardValue(),
                request.totalStock(),
                request.currentStock()
        );

        return rewardItem;
    }

    // 상품 목록 전체 조회
    public List<RewardItem> findAll() {
        return rewardItemRepsitory.findAll();
    }
}
