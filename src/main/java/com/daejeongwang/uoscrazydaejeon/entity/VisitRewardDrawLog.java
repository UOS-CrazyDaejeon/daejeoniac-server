package com.daejeongwang.uoscrazydaejeon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "visit_reward_draw_log",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_visit_reward_draw_receipt",
                columnNames = "receipt_id"
        )
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VisitRewardDrawLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_reward_item_id", nullable = false)
    private VisitRewardItem rewardItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "reward_item_type", nullable = false)
    private RewardItem.RewardItemType rewardItemType;

    @Column(nullable = false)
    private Integer rewardValue;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id", nullable = false, unique = true)
    private Receipt receipt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public VisitRewardDrawLog(
            Member member,
            VisitRewardItem rewardItem,
            RewardItem.RewardItemType rewardItemType,
            Integer rewardValue,
            Receipt receipt
    ) {
        this.member = member;
        this.rewardItem = rewardItem;
        this.rewardItemType = rewardItemType;
        this.rewardValue = rewardValue;
        this.receipt = receipt;
        this.createdAt = LocalDateTime.now();
    }
}
