package com.daejeongwang.uoscrazydaejeon.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeId;

    private String placeName;

    private String tag;

    @Column(name = "place_description", columnDefinition = "TEXT")
    private String placeDescription;

    private String placeAddress;

    private Double latitude;

    private Double longitude;

    private String gu;

    private String dong;

    private String categoryLarge;

    private String categoryMedium;

    private String categorySmall;
}