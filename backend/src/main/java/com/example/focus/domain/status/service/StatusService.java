package com.example.focus.domain.status.service;

import com.example.focus.domain.status.dto.SurvivalActionsResponseDto;

public interface StatusService {
    SurvivalActionsResponseDto getSurvivalActions(String stage, Double lat, Double lng);
}
