package com.example.focus.domain.map.dto;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LocationPinResponseDto {
    private double lat;
    private double lng;
    private String updatedAt;
    private String source;
    private boolean offline;

    public LocationPinResponseDto(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
        this.updatedAt = LocalDateTime.now().toString();
        this.source = "manual_pin";
        this.offline = false;
    }
}
