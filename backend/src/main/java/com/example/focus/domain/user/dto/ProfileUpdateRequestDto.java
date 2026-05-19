package com.example.focus.domain.user.dto;

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

    private String preferredLanguage; // ar, fr, wo, ma, fu

    @Valid
    private HealthProfileRequestDto healthInfo;

    private List<String> companions; // child, infant, pregnant, elderly, disabled
}