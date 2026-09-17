package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.dto.request.VisitRewardItemCreateRequest;
import com.daejeongwang.uoscrazydaejeon.dto.request.VisitRewardItemUpdateRequest;
import com.daejeongwang.uoscrazydaejeon.entity.VisitRewardItem;
import com.daejeongwang.uoscrazydaejeon.exception.ResourceNotFoundException;
import com.daejeongwang.uoscrazydaejeon.repository.VisitRewardItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitRewardItemService {

    private final VisitRewardItemRepository visitRewardItemRepository;

    @Transactional(readOnly = true)
    public List<VisitRewardItem> findAll() {
        return visitRewardItemRepository.findAll();
    }

    @Transactional
    public VisitRewardItem create(VisitRewardItemCreateRequest request) {
        validateValues(request.probability(), request.rewardValue(), request.totalStock());

        return visitRewardItemRepository.save(VisitRewardItem.builder()
                .probability(request.probability())
                .itemType(request.itemType())
                .rewardValue(request.rewardValue())
                .totalStock(request.totalStock())
                .currentStock(request.totalStock())
                .build());
    }

    @Transactional
    public VisitRewardItem update(Long itemId, VisitRewardItemUpdateRequest request) {
        VisitRewardItem item = visitRewardItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("방문 보상 항목이 존재하지 않습니다."));

        validateValues(request.probability(), request.rewardValue(), request.totalStock());
        if (request.currentStock() != null && request.currentStock() < 0) {
            throw new IllegalArgumentException("현재 재고는 0 이상이어야 합니다.");
        }
        if (request.totalStock() != null
                && request.currentStock() != null
                && request.currentStock() > request.totalStock()) {
            throw new IllegalArgumentException("현재 재고는 전체 재고를 초과할 수 없습니다.");
        }
        item.update(
                request.probability(),
                request.itemType(),
                request.rewardValue(),
                request.totalStock(),
                request.currentStock()
        );

        return item;
    }

    private void validateValues(Double probability, Integer rewardValue, Integer totalStock) {
        if (probability == null || probability < 0 || probability > 1) {
            throw new IllegalArgumentException("확률은 0 이상 1 이하로 입력해야 합니다.");
        }
        if (rewardValue == null || rewardValue < 0) {
            throw new IllegalArgumentException("보상 포인트는 0 이상이어야 합니다.");
        }
        if (totalStock != null && totalStock < 0) {
            throw new IllegalArgumentException("재고는 0 이상이어야 합니다.");
        }
    }
}
