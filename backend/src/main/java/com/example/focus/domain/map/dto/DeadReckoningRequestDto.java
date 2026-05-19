package com.example.focus.domain.map.dto;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DeadReckoningRequestDto {
    private double lastLat;
    private double lastLng;
    private double heading;
    private int steps;
    private Double strideLengthM;
}