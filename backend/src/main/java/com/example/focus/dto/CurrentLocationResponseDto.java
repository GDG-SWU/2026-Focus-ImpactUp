package com.example.focus.dto;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class) // accuracy_m 자동 치환
public class CurrentLocationResponseDto {
    private double lat;
    private double lng;
    private double accuracyM;
    private String source; // gps, dead_reckoning, manual_pin, cached
    private boolean offline;
}
