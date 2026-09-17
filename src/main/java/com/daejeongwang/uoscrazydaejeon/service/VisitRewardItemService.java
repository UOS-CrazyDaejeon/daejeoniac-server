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
        validateValues(request.probability(), request.rewardValue());

        return visitRewardItemRepository.save(VisitRewardItem.builder()
                .probability(request.probability())
                .itemType(request.itemType())
                .rewardValue(request.rewardValue())
                .build());
    }

    @Transactional
    public VisitRewardItem update(Long itemId, VisitRewardItemUpdateRequest request) {
        VisitRewardItem item = visitRewardItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("방문 보상 항목이 존재하지 않습니다."));

        validateValues(request.probability(), request.rewardValue());
        item.update(
                request.probability(),
                request.itemType(),
                request.rewardValue()
        );

        return item;
    }

    private void validateValues(Double probability, Integer rewardValue) {
        if (probability == null || probability < 0 || probability > 1) {
            throw new IllegalArgumentException("확률은 0 이상 1 이하로 입력해야 합니다.");
        }
        if (rewardValue == null || rewardValue < 0) {
            throw new IllegalArgumentException("보상 포인트는 0 이상이어야 합니다.");
        }
    }
}
