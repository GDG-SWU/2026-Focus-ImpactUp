package com.example.focus.domain.map.service;

import com.example.focus.domain.map.dto.DeadReckoningRequestDto;
import com.example.focus.domain.map.dto.DeadReckoningResponseDto;
import com.example.focus.domain.map.dto.FacilityListResponseDto;

public interface MapService {
    DeadReckoningResponseDto calculateDeadReckoning(DeadReckoningRequestDto request);
    FacilityListResponseDto getFacilities(String category, Double lat, Double lng, int radius);
}