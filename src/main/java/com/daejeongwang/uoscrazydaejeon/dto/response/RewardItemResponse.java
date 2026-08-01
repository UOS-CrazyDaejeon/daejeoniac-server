package com.daejeongwang.uoscrazydaejeon.dto.response;

import com.daejeongwang.uoscrazydaejeon.entity.RewardItem;

public record RewardItemResponse(
        RewardItem.RewardItemType itemType,
        Integer rewardValue,
        Integer currentStock
) {
    public static RewardItemResponse from(RewardItem rewardItem) {
        return new RewardItemResponse(
                rewardItem.getItemType(),
                rewardItem.getRewardValue(),
                rewardItem.getCurrentStock()
        );
    }

}
