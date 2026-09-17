package com.daejeongwang.uoscrazydaejeon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "visit_reward_item")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitRewardItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double probability;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RewardItem.RewardItemType itemType;

    private Integer rewardValue;

    private Integer totalStock;

    private Integer currentStock;

    public void update(
            Double probability,
            RewardItem.RewardItemType itemType,
            Integer rewardValue,
            Integer totalStock,
            Integer currentStock
    ) {
        this.probability = probability;
        this.itemType = itemType;
        this.rewardValue = rewardValue;
        this.totalStock = totalStock;
        this.currentStock = currentStock;
    }

    public void decreaseStock() {
        if (currentStock != null && currentStock <= 0) {
            throw new IllegalStateException("방문 보상 재고가 없습니다.");
        }

        if (currentStock != null) {
            currentStock--;
        }
    }
}
