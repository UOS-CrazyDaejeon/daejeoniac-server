package com.daejeongwang.uoscrazydaejeon.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardDrawLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 누가 뽑았는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 어떤 종류의 아이템인지?
    @Enumerated(EnumType.STRING)
    @Column(name = "reward_item_type", nullable = false)
    private RewardItem.RewardItemType rewardItemType;

    // 지급 값
    @Column(nullable = false)
    private Integer rewardValue;

    // 뭘 뽑았는지?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_item_id", nullable = false)
    private RewardItem rewardItem;

    // 어떤 영수증에 대한 뽑기인지?
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id", nullable = false, unique = true)
    private Receipt receipt;

    // 뽑은 시점
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}