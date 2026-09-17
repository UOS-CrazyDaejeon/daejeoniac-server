package com.daejeongwang.uoscrazydaejeon.dto.response;

import com.daejeongwang.uoscrazydaejeon.entity.RewardDrawLog;
import com.daejeongwang.uoscrazydaejeon.entity.RewardItem;
import com.daejeongwang.uoscrazydaejeon.entity.VisitRewardDrawLog;

import java.time.LocalDateTime;

public record RewardDrawResponse(
        Long rewardDrawLogId,
        Long receiptId,
        Long rewardItemId,
        RewardItem.RewardItemType itemType,
        Integer rewardValue,
        LocalDateTime drawnAt
) {
    public static RewardDrawResponse from(RewardDrawLog rewardDrawLog)
    {
        return new RewardDrawResponse(
                rewardDrawLog.getId(),
                rewardDrawLog.getReceipt().getId(),
                rewardDrawLog.getRewardItem().getId(),
                rewardDrawLog.getRewardItemType(),
                rewardDrawLog.getRewardValue(),
                rewardDrawLog.getCreatedAt()
        );
    }

    public static RewardDrawResponse from(VisitRewardDrawLog rewardDrawLog) {
        return new RewardDrawResponse(
                rewardDrawLog.getId(),
                rewardDrawLog.getReceipt().getId(),
                rewardDrawLog.getRewardItem().getId(),
                rewardDrawLog.getRewardItemType(),
                rewardDrawLog.getRewardValue(),
                rewardDrawLog.getCreatedAt()
        );
    }
}
