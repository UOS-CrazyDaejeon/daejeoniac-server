package com.daejeongwang.uoscrazydaejeon.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_visited_place_member_place_date", columnNames = {"member_id", "place_id", "visited_date"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class VisitedPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long visitedPlaceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(nullable = false, updatable = false)
    private LocalDateTime visitedAt;

    @Column(name = "visited_date", nullable = false, updatable = false)
    private LocalDate visitedDate;

    @PrePersist
    private void prePersist() {
        if (this.visitedAt == null) {
            this.visitedAt = LocalDateTime.now();
        }
        if (this.visitedDate == null) {
            this.visitedDate = this.visitedAt.toLocalDate();
        }
    }

}
