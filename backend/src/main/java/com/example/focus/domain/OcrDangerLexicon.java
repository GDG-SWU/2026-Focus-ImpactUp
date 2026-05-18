package com.example.focus.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ocr_danger_lexicon")
@Getter
@NoArgsConstructor
public class OcrDangerLexicon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "substance_name", nullable = false, length = 50)
    private String substanceName; // OCR 텍스트 매칭용 (예: "Amoxicillin")

    @Column(name = "target_type", nullable = false, length = 50)
    private String targetType; // 분류 식별용 ("ALLERGY" 또는 "CONDITION")

    @Column(name = "mapping_code", nullable = false, length = 50)
    private String mappingCode; // 시스템 연계 코드 (예: "penicillin", "diabetes")
}