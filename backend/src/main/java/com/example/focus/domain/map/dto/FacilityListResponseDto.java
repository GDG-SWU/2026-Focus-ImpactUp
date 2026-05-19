package com.example.focus.domain.map.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class FacilityListResponseDto {
    private List<FacilitySummaryDto> facilities;
    private int total;
    private boolean offline;
    private String cachedAt;
}