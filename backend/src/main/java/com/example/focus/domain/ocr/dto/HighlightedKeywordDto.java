package com.example.focus.domain.ocr.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class HighlightedKeywordDto {
    private String keyword;
    private String type;
    private boolean bold;
    private String highlightColor;
}