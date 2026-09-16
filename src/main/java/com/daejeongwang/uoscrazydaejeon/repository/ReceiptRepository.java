package com.daejeongwang.uoscrazydaejeon.repository;

import com.daejeongwang.uoscrazydaejeon.entity.Receipt;
import com.daejeongwang.uoscrazydaejeon.entity.VisitedPlace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    @Query("select receipt.objectKey from Receipt receipt where receipt.visitedPlace.member.id = :memberId")
    List<String> findObjectKeysByMemberId(@Param("memberId") Long memberId);

    Optional<Receipt> findByReceiptUuid(UUID receiptUuid);
    Optional<Receipt> findByIdAndVisitedPlace_Member_Id(Long receiptId, Long memberId);
    boolean existsByVisitedPlaceAndVerifyStatus(
            VisitedPlace visitedPlace,
            Receipt.ReceiptStatus verifyStatus
    );
    Optional<Receipt> findFirstByVisitedPlaceAndVerifyStatusOrderByRequestedAtDesc(
            VisitedPlace visitedPlace,
            Receipt.ReceiptStatus verifyStatus
    );

    List<Receipt> findAllByVisitedPlaceInAndVerifyStatusIn(
            List<VisitedPlace> visitedPlaces,
            List<Receipt.ReceiptStatus> verifyStatuses
    );

    Optional<Receipt> findByVisitedPlace(VisitedPlace visitedPlace);

    Optional<Receipt> findByVisitedPlace_IdAndVisitedPlace_Member_Id(Long visitedPlaceId, Long memberId);

    Page<Receipt> findAllByVisitedPlace_Member_IdAndVerifyStatusOrderByRequestedAtDesc(
            Long memberId,
            Receipt.ReceiptStatus verifyStatus,
            Pageable pageable
    );

    void deleteAllByVisitedPlace_Member_Id(Long memberId);
}
