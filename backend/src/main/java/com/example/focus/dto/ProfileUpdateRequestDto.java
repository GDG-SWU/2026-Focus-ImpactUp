package com.example.focus.dto;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProfileUpdateRequestDto {

    // PATCH이므로 수정을 원치 않을 때 null이 들어올 수 있도록 Validation 제거
    private String preferredLanguage; // ar, fr, wo, ma, fu

    @Valid
    private HealthProfileRequestDto healthInfo;

    private List<String> companions; // child, infant, pregnant, elderly, disabled
}