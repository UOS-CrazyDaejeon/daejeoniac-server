package com.daejeongwang.uoscrazydaejeon.dto.response;

import com.daejeongwang.uoscrazydaejeon.entity.RewardDrawLog;
import com.daejeongwang.uoscrazydaejeon.entity.RewardItem;

import java.time.LocalDateTime;

public record RewardDrawLogResponse(
        Long rewardDrawLogId,
        Long receiptId,
        String placeName,
        Long rewardItemId,
        RewardItem.RewardItemType rewardItemType,
        Integer rewardValue,
        LocalDateTime drawnAt
) {
    public static RewardDrawLogResponse from(RewardDrawLog log) {
        return new RewardDrawLogResponse(
                log.getId(),
                log.getReceipt().getId(),
                log.getReceipt().getVisitedPlace().getPlace().getPlaceName(),
                log.getRewardItemId(),
                log.getRewardItemType(),
                log.getRewardValue(),
                log.getCreatedAt()
        );
    }
}
