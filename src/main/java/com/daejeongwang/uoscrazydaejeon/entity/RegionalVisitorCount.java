package com.daejeongwang.uoscrazydaejeon.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(name = "uk_regional_visitor_count_signgu_date", columnNames = {"signgu_code", "date"})})
public class RegionalVisitorCount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "regional_visitor_count_id")
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "signgu_code", nullable = false, length = 5)
    private String signguCode;

    @Column(nullable = false)
    private String signguName;

    //현지인
    @Column(precision = 15, scale = 2)
    private BigDecimal  localVisitorCount;

    //외지인
    @Column(precision = 15, scale = 2)
    private BigDecimal  outsiderVisitorCount;

    //외국인
    @Column(precision = 15, scale = 2)
    private BigDecimal foreignVisitorCount;

    public void updateVisitorCounts(
            BigDecimal localVisitorCount,
            BigDecimal outsiderVisitorCount,
            BigDecimal foreignVisitorCount
    ) {
        this.localVisitorCount = localVisitorCount;
        this.outsiderVisitorCount = outsiderVisitorCount;
        this.foreignVisitorCount = foreignVisitorCount;
    }
}
