package com.example.focus.service;

import com.example.focus.dto.DeadReckoningRequestDto;
import com.example.focus.dto.DeadReckoningResponseDto;
import com.example.focus.dto.FacilityListResponseDto;

public interface MapService {
    DeadReckoningResponseDto calculateDeadReckoning(DeadReckoningRequestDto request);
    FacilityListResponseDto getFacilities(String category, Double lat, Double lng, int radius);
}