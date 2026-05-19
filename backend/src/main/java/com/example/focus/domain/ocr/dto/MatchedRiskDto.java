package com.example.focus.domain.ocr.dto;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MatchedRiskDto {
    private String keyword;
    private String matchedProfileField;
    private String matchedValue;
    private String warningMessage;
}