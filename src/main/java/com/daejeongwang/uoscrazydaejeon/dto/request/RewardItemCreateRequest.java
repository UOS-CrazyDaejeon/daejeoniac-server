package com.daejeongwang.uoscrazydaejeon.dto.request;

import com.daejeongwang.uoscrazydaejeon.entity.RewardItem;
import lombok.Getter;

@Getter
public class RewardItemCreateRequest {

    private Double probability;

    // 현재는 POINT로 고정
    private RewardItem.RewardItemType itemType;

    // 지금은 포인트 밖에 없어서 int지만 나중에 String으로 바꿔야하지않을까?
    private Integer rewardValue;

    // 해당 상품의 전체 수량
    private Integer totalStock;

}
