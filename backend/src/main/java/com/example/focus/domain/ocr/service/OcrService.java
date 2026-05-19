package com.example.focus.domain.ocr.service;

import com.example.focus.domain.ocr.dto.RiskCheckRequestDto;
import com.example.focus.domain.ocr.dto.RiskCheckResponseDto;

public interface OcrService {
    RiskCheckResponseDto evaluateOcrTextWithUserProfile(RiskCheckRequestDto request);
}