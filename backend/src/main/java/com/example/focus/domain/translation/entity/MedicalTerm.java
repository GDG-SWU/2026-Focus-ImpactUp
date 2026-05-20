package com.example.focus.domain.translation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medical_terms")
@Getter
@NoArgsConstructor
public class MedicalTerm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "term_code", nullable = false)
    private String termCode;

    @Column(name = "lang_code", nullable = false)
    private String langCode;

    @Column(name = "translated_text", nullable = false)
    private String translatedText;
}