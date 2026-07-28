package com.daejeongwang.uoscrazydaejeon.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class VisitVerificationRequest {

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    @NotNull
    private LocalDateTime measuredAt;

    @NotNull
    private Double accuracy;
}
