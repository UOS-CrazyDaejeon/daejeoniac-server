package com.daejeongwang.uoscrazydaejeon.dto.response;

import com.daejeongwang.uoscrazydaejeon.entity.RewardItem;

public record RewardItemResponse(
        RewardItem.RewardItemType itemType,
        Integer rewardValue
) {
    public static RewardItemResponse from(RewardItem rewardItem) {
        return new RewardItemResponse(
                rewardItem.getItemType(),
                rewardItem.getRewardValue()
        );
    }

}
