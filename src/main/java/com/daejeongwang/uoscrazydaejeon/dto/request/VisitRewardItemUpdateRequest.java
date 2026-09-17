package com.daejeongwang.uoscrazydaejeon.dto.request;

import com.daejeongwang.uoscrazydaejeon.entity.RewardItem;

public record VisitRewardItemUpdateRequest(
        Double probability,
        RewardItem.RewardItemType itemType,
        Integer rewardValue,
        Integer totalStock,
        Integer currentStock
) {
}
