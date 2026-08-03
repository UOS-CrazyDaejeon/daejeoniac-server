package com.daejeongwang.uoscrazydaejeon.dto.request;

import com.daejeongwang.uoscrazydaejeon.entity.RewardItem;
import lombok.Getter;

public record  RewardItemCreateRequest(
   Double prbability,
   RewardItem.RewardItemType itemType,
   Integer rewardValue,
   Integer totalStock
) {
}
