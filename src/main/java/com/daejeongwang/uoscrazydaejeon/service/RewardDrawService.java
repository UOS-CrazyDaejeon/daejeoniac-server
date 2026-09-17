package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.dto.response.RewardDrawLogResponse;
import com.daejeongwang.uoscrazydaejeon.dto.response.RewardDrawResponse;
import com.daejeongwang.uoscrazydaejeon.entity.Member;
import com.daejeongwang.uoscrazydaejeon.entity.Receipt;
import com.daejeongwang.uoscrazydaejeon.entity.RewardDrawLog;
import com.daejeongwang.uoscrazydaejeon.entity.RewardItem;
import com.daejeongwang.uoscrazydaejeon.entity.VisitRewardDrawLog;
import com.daejeongwang.uoscrazydaejeon.entity.VisitRewardItem;
import com.daejeongwang.uoscrazydaejeon.repository.ReceiptRepository;
import com.daejeongwang.uoscrazydaejeon.repository.RewardDrawLogRepository;
import com.daejeongwang.uoscrazydaejeon.repository.RewardItemRepository;
import com.daejeongwang.uoscrazydaejeon.repository.VisitedPlaceRepository;
import com.daejeongwang.uoscrazydaejeon.repository.VisitRewardDrawLogRepository;
import com.daejeongwang.uoscrazydaejeon.repository.VisitRewardItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardDrawService {

    private final RewardDrawLogRepository rewardDrawLogRepository;
    private final VisitedPlaceRepository visitedPlaceRepository;
    private final ReceiptRepository receiptRepository;
    private final RewardItemRepository rewardItemRepository;
    private final VisitRewardItemRepository visitRewardItemRepository;
    private final VisitRewardDrawLogRepository visitRewardDrawLogRepository;

    // 상품 뽑기 로직
    @Transactional
    public RewardDrawResponse drawReward(Long memberId, Long visitedPlaceId) {
        Receipt receipt = receiptRepository.findByVisitedPlace_IdAndVisitedPlace_Member_Id(visitedPlaceId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("승인된 영수증이 아니거나 방문한 장소 아닙니다."));

        if(receipt.getVerifyStatus() != Receipt.ReceiptStatus.APPROVED) {
            throw new IllegalStateException("승인된 영수증만 뽑기할 수 있습니다.");
        }

        if (receipt.getVerificationType() == Receipt.VerificationType.VISIT) {
            return drawVisitReward(memberId, receipt);
        }

        if(rewardDrawLogRepository.existsByReceipt(receipt)) {
            throw new IllegalStateException("이미 사용된 영수증입니다.");
        }

        List<RewardItem> rewardItems = rewardItemRepository.findByCurrentStockGreaterThan(0);
        RewardItem selectedRewardItem = randomSelectItem(rewardItems);
        selectedRewardItem.decreaseStock();
        Member member = receipt.getVisitedPlace().getMember();
        member.addPoint(selectedRewardItem.getRewardValue());

        RewardDrawLog rewardDrawLog = RewardDrawLog.builder()
                .member(member)
                .rewardItemType(selectedRewardItem.getItemType())
                .rewardItem(selectedRewardItem)
                .rewardValue(selectedRewardItem.getRewardValue())
                .receipt(receipt)
                .build();

        RewardDrawLog savedLog = rewardDrawLogRepository.save(rewardDrawLog);

        return RewardDrawResponse.from(savedLog);
    }

    private RewardDrawResponse drawVisitReward(Long memberId, Receipt receipt) {
        if (receipt.getVisitedPlace().getMember().getId() != memberId) {
            throw new IllegalArgumentException("본인의 방문 기록이 아닙니다.");
        }

        if (visitRewardDrawLogRepository.existsByReceipt(receipt)) {
            throw new IllegalStateException("이미 사용된 방문 인증입니다.");
        }

        List<VisitRewardItem> rewardItems = visitRewardItemRepository
                .findByCurrentStockGreaterThanOrCurrentStockIsNull(0);
        if (rewardItems.isEmpty()) {
            throw new IllegalStateException("등록된 방문 보상이 없습니다.");
        }

        VisitRewardItem selectedRewardItem = randomSelectVisitItem(rewardItems);
        selectedRewardItem.decreaseStock();

        Member member = receipt.getVisitedPlace().getMember();
        member.addPoint(selectedRewardItem.getRewardValue());

        VisitRewardDrawLog rewardDrawLog = VisitRewardDrawLog.builder()
                .member(member)
                .rewardItem(selectedRewardItem)
                .rewardItemType(selectedRewardItem.getItemType())
                .rewardValue(selectedRewardItem.getRewardValue())
                .receipt(receipt)
                .build();

        return RewardDrawResponse.from(visitRewardDrawLogRepository.save(rewardDrawLog));
    }

    // 내 뽑기 기록 조회
    public List<RewardDrawLogResponse> findMyRewardDrawLogs(Long memberId) {
        List<RewardDrawLog> rewardDrawLogs = rewardDrawLogRepository.findAllByMember_IdOrderByCreatedAtDesc(memberId);

        return rewardDrawLogs
                .stream()
                .map(RewardDrawLogResponse::from)
                .toList();
    }

    // 랜덤 선택
    private RewardItem randomSelectItem(List<RewardItem> rewardItems) {
        double random = Math.random();
        double cumulativeProbability = 0.0;

        for(RewardItem rewardItem : rewardItems) {
            cumulativeProbability += rewardItem.getProbability();

            if(random <= cumulativeProbability) {
                return rewardItem;
            }
        }

        return rewardItems.get(rewardItems.size() - 1);
    }

    private VisitRewardItem randomSelectVisitItem(List<VisitRewardItem> rewardItems) {
        double random = Math.random();
        double cumulativeProbability = 0.0;

        for (VisitRewardItem rewardItem : rewardItems) {
            cumulativeProbability += rewardItem.getProbability();
            if (random <= cumulativeProbability) {
                return rewardItem;
            }
        }

        return rewardItems.get(rewardItems.size() - 1);
    }

}
