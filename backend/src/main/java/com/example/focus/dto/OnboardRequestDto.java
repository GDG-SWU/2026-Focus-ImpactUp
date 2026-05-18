package com.example.focus.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OnboardRequestDto {

    @NotBlank(message = "locale은 필수입니다.")
    private String locale; // ar-SD

    @NotBlank(message = "preferred_language는 필수입니다.")
    private String preferredLanguage; // ar, fr, wo, ma, fu

    @Valid
    @NotNull(message = "health_info는 필수입니다.")
    private HealthProfileRequestDto healthInfo;

    // 동반자가 없는 경우 초기화
    private List<String> companions = new ArrayList<>(); // child, infant, pregnant, elderly, disabled
}