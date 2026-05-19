package com.example.focus.domain.map.dto;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CurrentLocationResponseDto {
    private double lat;
    private double lng;
    private double accuracyM;
    private String source;
    private boolean offline;
}
