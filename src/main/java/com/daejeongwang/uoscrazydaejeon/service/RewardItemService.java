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
    public RewardItem updateRewardItem(Long rewardItemId, RewardItemUpdateRequest request) {
        RewardItem rewardItem = rewardItemRepsitory.findById(rewardItemId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 상품이 존재하지 않습니다."));

        validateProbabilitySumForUpdate(rewardItemId, request.probability());

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

    // 확률값 검증 메서드
    // TODO : 오차 범위 발생할 수 있다고 하는데, 오차 범위 허용 해주는 것도 필요해보임.
    private void validateProbabilitySumForUpdate(Long rewardItemId, Double newProbability) {
        double totalProbabilityExceptCurrent = rewardItemRepsitory.findAll()
                .stream()
                .filter(rewardItem -> !rewardItem.getId().equals(rewardItemId))
                .mapToDouble(RewardItem::getProbability)
                .sum();

        if (totalProbabilityExceptCurrent + newProbability > 1.0) {
            throw new IllegalArgumentException("상품 확률 총합은 1을 초과할 수 없습니다.");
        }
    }
}
