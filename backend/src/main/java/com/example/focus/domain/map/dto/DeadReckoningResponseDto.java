package com.example.focus.domain.map.dto;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)

public class DeadReckoningResponseDto {
    private double estimatedLat;
    private double estimatedLng;
    private double confidence;
    private String source;
    private boolean offline;
}