package com.example.focus.domain.ocr.controller;

import com.example.focus.domain.ocr.dto.HighlightedKeywordDto;
import com.example.focus.domain.ocr.dto.OcrScanResponseDto;
import com.example.focus.domain.ocr.dto.RiskCheckRequestDto;
import com.example.focus.domain.ocr.dto.RiskCheckResponseDto;
import com.example.focus.domain.ocr.service.OcrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ocr")
@RequiredArgsConstructor
public class OcrController {

    private final OcrService ocrService;

    /**
     * POST /ocr/scan (카메라 캡처 파일 업로드 및 텍스트 추출)
     */
    @PostMapping(value = "/scan", consumes = "multipart/form-data")
    public ResponseEntity<OcrScanResponseDto> scanImage(
            @RequestPart("image") MultipartFile image,
            @RequestParam(required = false, defaultValue = "ko") String targetLanguage) {

        OcrScanResponseDto response = ocrService.processOcrScan(image, targetLanguage);

        return ResponseEntity.ok(response);
    }

    /**
     * POST /ocr/risk-check (추출 텍스트와 데이터베이스 사전 위험성 매칭)
     */
    @PostMapping("/risk-check")
    public ResponseEntity<RiskCheckResponseDto> checkRisk(@RequestBody RiskCheckRequestDto request) {
        RiskCheckResponseDto result = ocrService.evaluateOcrTextWithUserProfile(request);
        return ResponseEntity.ok(result);
    }
}
