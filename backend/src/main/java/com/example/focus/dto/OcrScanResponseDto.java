package com.example.focus.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class OcrScanResponseDto {
    private String rawText;
    private String translatedText;
    private List<HighlightedKeywordDto> highlightedKeywords;
    private String disclaimer;
    private boolean offline;
}