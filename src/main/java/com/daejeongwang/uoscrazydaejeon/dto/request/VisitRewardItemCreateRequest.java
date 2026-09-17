package com.daejeongwang.uoscrazydaejeon.dto.request;

import com.daejeongwang.uoscrazydaejeon.entity.RewardItem;

public record VisitRewardItemCreateRequest(
        Double probability,
        RewardItem.RewardItemType itemType,
        Integer rewardValue,
        Integer totalStock
) {
}
