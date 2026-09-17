package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.dto.response.VisitedPlaceListResponse;
import com.daejeongwang.uoscrazydaejeon.entity.Receipt;
import com.daejeongwang.uoscrazydaejeon.entity.VisitedPlace;
import com.daejeongwang.uoscrazydaejeon.repository.ReceiptRepository;
import com.daejeongwang.uoscrazydaejeon.repository.RewardDrawLogRepository;
import com.daejeongwang.uoscrazydaejeon.repository.VisitRewardDrawLogRepository;
import com.daejeongwang.uoscrazydaejeon.repository.VisitedPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Instant;
import java.time.Clock;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitedPlaceService {
    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");
    private final VisitedPlaceRepository visitedPlaceRepository;
    private final ReceiptRepository receiptRepository;
    private final RewardDrawLogRepository rewardDrawLogRepository;
    private final VisitRewardDrawLogRepository visitRewardDrawLogRepository;
    private final Clock clock;

    private static final Duration PENDING_VALID_DURATION = Duration.ofMinutes(5);

    public List<VisitedPlaceListResponse> getMyVisitedPlaces(Long memberId) {
        Instant now = clock.instant();
        LocalDate today = now.atZone(SEOUL).toLocalDate();

        List<VisitedPlace> visitedPlaces = visitedPlaceRepository
                .findAllByMember_IdAndVisitedDateOrderByVisitedAtDesc(memberId, today);
        if (visitedPlaces.isEmpty()) {
            return List.of();
        }

        List<Receipt> receipts = receiptRepository.findAllByVisitedPlaceInAndVerifyStatusIn(
                visitedPlaces,
                List.of(Receipt.ReceiptStatus.APPROVED, Receipt.ReceiptStatus.PENDING)
        );
        Map<Long, List<Receipt>> receiptMap = receipts.stream().collect(Collectors.groupingBy(
                receipt -> receipt.getVisitedPlace().getId()
        ));
        Set<Long> usedReceiptIds;
        if (receipts.isEmpty()) {
            usedReceiptIds = Set.of();
        } else {
            usedReceiptIds = new java.util.HashSet<>(
                    rewardDrawLogRepository.findUsedReceiptIdsByReceiptIn(receipts)
            );
            usedReceiptIds.addAll(
                    visitRewardDrawLogRepository.findUsedReceiptIdsByReceiptIn(receipts)
            );
        }

        return visitedPlaces.stream()
                .map(visitedPlace -> VisitedPlaceListResponse.builder()
                        .visitedPlaceId(visitedPlace.getId())
                        .placeId(visitedPlace.getPlace().getId())
                        .placeName(visitedPlace.getPlace().getPlaceName())
                        .visitedAt(visitedPlace.getVisitedAt())
                        .receiptAvailability(
                                getReceiptAvailability(
                                        visitedPlace,
                                        receiptMap.getOrDefault(
                                                visitedPlace.getId(),
                                                List.of()
                                        ),
                                        usedReceiptIds,
                                        now
                                )
                        )
                        .build())
                .filter(response -> response.getReceiptAvailability()
                        != VisitedPlaceListResponse.ReceiptAvailability.UNAVAILABLE)
                .sorted(Comparator.comparingInt(response -> receiptAvailabilityPriority(
                        response.getReceiptAvailability()
                )))
                .toList();

    }

    private int receiptAvailabilityPriority(VisitedPlaceListResponse.ReceiptAvailability availability) {
        return switch (availability) {
            case APPROVED -> 0;
            case PROCESSING -> 1;
            case AVAILABLE -> 2;
            case UNAVAILABLE -> 3;
        };
    }

    private VisitedPlaceListResponse.ReceiptAvailability getReceiptAvailability(
            VisitedPlace visitedPlace,
            List<Receipt> receipts,
            Set<Long> usedReceiptIds,
            Instant now
    ) {
        List<Receipt> approvedReceipts = receipts.stream()
                .filter(receipt -> receipt.getVerifyStatus() == Receipt.ReceiptStatus.APPROVED)
                .toList();
        boolean hasUnusedApprovedReceipt = approvedReceipts.stream()
                .anyMatch(receipt -> !usedReceiptIds.contains(receipt.getId()));
        if (hasUnusedApprovedReceipt) {
            return VisitedPlaceListResponse.ReceiptAvailability.APPROVED;
        }
        if (!approvedReceipts.isEmpty()) {
            return VisitedPlaceListResponse.ReceiptAvailability.UNAVAILABLE;
        }

        boolean hasValidPendingReceipt = receipts.stream()
                .anyMatch(receipt -> receipt.getVerifyStatus() == Receipt.ReceiptStatus.PENDING
                                && receipt.getRequestedAt()
                                .plus(PENDING_VALID_DURATION)
                                .isAfter(now)
                );
        if (hasValidPendingReceipt) {
            return VisitedPlaceListResponse.ReceiptAvailability.PROCESSING;
        }

        LocalDate today = now.atZone(SEOUL).toLocalDate();

        if (visitedPlace.getVisitedDate().equals(today)) {
            return VisitedPlaceListResponse.ReceiptAvailability.AVAILABLE;
        }

        return VisitedPlaceListResponse.ReceiptAvailability.UNAVAILABLE;
    }

}
