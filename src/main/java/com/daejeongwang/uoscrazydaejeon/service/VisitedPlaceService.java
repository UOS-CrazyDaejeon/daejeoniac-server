package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.dto.response.VisitedPlaceListResponse;
import com.daejeongwang.uoscrazydaejeon.entity.Receipt;
import com.daejeongwang.uoscrazydaejeon.entity.VisitedPlace;
import com.daejeongwang.uoscrazydaejeon.repository.ReceiptRepository;
import com.daejeongwang.uoscrazydaejeon.repository.VisitedPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitedPlaceService {
    private final VisitedPlaceRepository visitedPlaceRepository;
    private final ReceiptRepository receiptRepository;

    private static final Duration PENDING_VALID_DURATION = Duration.ofMinutes(10);

    public List<VisitedPlaceListResponse> getMyVisitedPlaces() {
        Long memberId = 1L;
        LocalDateTime now = LocalDateTime.now();

        List<VisitedPlace> visitedPlaces = visitedPlaceRepository.findAllByMember_IdOrderByVisitedAtDesc(memberId);
        if (visitedPlaces.isEmpty()) {
            return List.of();
        }

        List<Receipt> receipts = receiptRepository.findAllByVisitedPlaceInAndVerifyStatusIn(
                visitedPlaces,
                List.of(Receipt.ReceiptStatus.APPROVED, Receipt.ReceiptStatus.PENDING)
        );
        Map<Long, List<Receipt>> receiptMap = receipts.stream().collect(Collectors.groupingBy(
                receipt -> receipt.getVisitedPlace().getVisitedPlaceId()
        ));

        return visitedPlaces.stream()
                .map(visitedPlace -> VisitedPlaceListResponse.builder()
                        .visitedPlaceId(visitedPlace.getVisitedPlaceId())
                        .placeId(visitedPlace.getPlace().getPlaceId())
                        .placeName(visitedPlace.getPlace().getPlaceName())
                        .visitedAt(visitedPlace.getVisitedAt())
                        .receiptAvailability(
                                getReceiptAvailability(
                                        visitedPlace,
                                        receiptMap.getOrDefault(
                                                visitedPlace.getVisitedPlaceId(),
                                                List.of()
                                        ),
                                        now
                                )
                        )
                        .build())
                .toList();

    }

    private VisitedPlaceListResponse.ReceiptAvailability getReceiptAvailability(VisitedPlace visitedPlace, List<Receipt> receipts, LocalDateTime now) {
        boolean hasApprovedReceipt = receipts.stream()
                .anyMatch(receipt -> receipt.getVerifyStatus() == Receipt.ReceiptStatus.APPROVED);
        if (hasApprovedReceipt) {
            return VisitedPlaceListResponse.ReceiptAvailability.APPROVED;
        }

        boolean hasValidPendingReceipt = receipts.stream()
                .anyMatch(receipt -> receipt.getVerifyStatus() == Receipt.ReceiptStatus.PENDING
                                && receipt.getCreatedAt()
                                .plus(PENDING_VALID_DURATION)
                                .isAfter(now)
                );
        if (hasValidPendingReceipt) {
            return VisitedPlaceListResponse.ReceiptAvailability.PROCESSING;
        }

        LocalDate today = now.toLocalDate();

        if (visitedPlace.getVisitedDate().equals(today)) {
            return VisitedPlaceListResponse.ReceiptAvailability.AVAILABLE;
        }

        return VisitedPlaceListResponse.ReceiptAvailability.UNAVAILABLE;
    }

}
