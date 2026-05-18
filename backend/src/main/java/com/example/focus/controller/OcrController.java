package com.example.focus.controller;

import com.example.focus.dto.HighlightedKeywordDto;
import com.example.focus.dto.OcrScanResponseDto;
import com.example.focus.dto.RiskCheckRequestDto;
import com.example.focus.dto.RiskCheckResponseDto;
import com.example.focus.service.OcrService;
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

//    public OcrController(OcrService ocrService) {
//        this.ocrService = ocrService;
//    }

    /**
     * POST /ocr/scan (카메라 캡처 파일 업로드 및 텍스트 추출)
     */
    @PostMapping(value = "/scan", consumes = "multipart/form-data")
    public ResponseEntity<OcrScanResponseDto> scanImage(
            @RequestPart("image") MultipartFile image,
            @RequestParam(required = false) String targetLanguage) {

        // 10MB 크기 유효성 검사 예시 (명세서의 413 코드 수용 흐름)
        if (image.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("FILE_TOO_LARGE");
        }

        HighlightedKeywordDto keyword = new HighlightedKeywordDto("Penicillin", "warning", true, "red");
        OcrScanResponseDto response = new OcrScanResponseDto(
                "Amoxicillin 500mg - contains Penicillin",
                "아목시실린 500mg - 페니실린 성분 포함",
                List.of(keyword),
                "참고용으로만 사용하세요. 의료 판단에 사용하지 마세요.",
                true
        );

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
