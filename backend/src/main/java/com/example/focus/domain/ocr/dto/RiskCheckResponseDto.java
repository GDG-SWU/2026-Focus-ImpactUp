package com.example.focus.domain.ocr.dto;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RiskCheckResponseDto {
    private boolean riskDetected;
    private String riskLevel;
    private List<MatchedRiskDto> matchedRisks;
    private boolean triggerHaptic;
    private boolean triggerAlertBanner;
    private boolean offline;
}