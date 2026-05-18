package com.example.focus.service;

import com.example.focus.dto.RiskCheckRequestDto;
import com.example.focus.dto.RiskCheckResponseDto;

public interface OcrService {
    RiskCheckResponseDto evaluateOcrTextWithUserProfile(RiskCheckRequestDto request);
}