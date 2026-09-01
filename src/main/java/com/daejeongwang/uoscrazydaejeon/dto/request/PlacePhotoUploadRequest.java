package com.daejeongwang.uoscrazydaejeon.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PlacePhotoUploadRequest {

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    @NotNull
    private Double accuracy;

    @NotNull
    private LocalDateTime measuredAt;
}
