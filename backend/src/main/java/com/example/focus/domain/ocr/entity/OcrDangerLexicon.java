package com.example.focus.domain.ocr.entity;

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
    private String substanceName;

    @Column(name = "target_type", nullable = false, length = 50)
    private String targetType;

    @Column(name = "mapping_code", nullable = false, length = 50)
    private String mappingCode;
}