package com.example.focus.dto;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MatchedRiskDto {
    private String keyword;
    private String matchedProfileField; // allergy, condition
    private String matchedValue;
    private String warningMessage;
}