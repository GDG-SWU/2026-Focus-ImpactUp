package com.example.focus.domain.ocr.service;

import com.example.focus.domain.ocr.dto.OcrScanResponseDto;
import com.example.focus.domain.ocr.dto.RiskCheckRequestDto;
import com.example.focus.domain.ocr.dto.RiskCheckResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface OcrService {

    OcrScanResponseDto processOcrScan(MultipartFile image, String targetLanguage);

    RiskCheckResponseDto evaluateOcrTextWithUserProfile(RiskCheckRequestDto request);
}