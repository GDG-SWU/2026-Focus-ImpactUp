package com.example.focus.domain.user.dto;

import com.example.focus.domain.user.entity.HealthProfile;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class HealthProfileResponseDto {

    private List<String> conditions = new ArrayList<>();
    private List<String> allergies = new ArrayList<>();

    public HealthProfileResponseDto(HealthProfile healthProfile) {
        if (healthProfile != null) {
            this.conditions = new ArrayList<>(healthProfile.getConditions());
            this.allergies = new ArrayList<>(healthProfile.getAllergies());
        }
    }
}
