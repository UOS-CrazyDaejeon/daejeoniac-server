package com.daejeongwang.uoscrazydaejeon.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardItem {

    public enum RewardItemType {
        POINT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double probability;

    // 상품의 종류 -> 포인트, 상품권 등...
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RewardItemType itemType;

    // 지금은 포인트 밖에 없어서 int지만 나중에 String으로 바꿔야하지않을까?
    private Integer rewardValue;

    // 해당 상품의 전체 수량
    private Integer totalStock;

    // 현재 남은 총 수량
    private Integer currentStock;

    public RewardItem updateRewardItem(
            Double probability,
            Integer rewardValue,
            Integer totalStock,
            Integer currentStock
    ) {
        this.probability = probability;
        this.rewardValue = rewardValue;
        this.totalStock = totalStock;
        this.currentStock = currentStock;

        return this;
    }

    public void decreaseStock() {
        if (currentStock <= 0) {
            throw new IllegalStateException("재고가 없습니다.");
        }

        this.currentStock--;
    }
}
