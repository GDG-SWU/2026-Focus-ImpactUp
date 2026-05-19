package com.example.focus.domain.status.dto;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SurvivalActionsResponseDto {
    private String stage;
    private List<Map<String, Object>> actions;
    private boolean offline;
    private String cachedAt;

    public SurvivalActionsResponseDto(String stage, List<Map<String, Object>> actions, boolean offline, String cachedAt) {
        this.stage = stage;
        this.actions = actions;
        this.offline = offline;
        this.cachedAt = cachedAt;
    }
}
