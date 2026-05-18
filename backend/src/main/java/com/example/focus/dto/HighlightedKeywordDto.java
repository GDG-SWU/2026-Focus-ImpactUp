package com.example.focus.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class HighlightedKeywordDto {
    private String keyword;
    private String type; // dosage, warning, ingredient, instruction
    private boolean bold;
    private String highlightColor; // red, yellow, blue, none
}