package com.example.focus.domain.translation.dto;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PhraseCardDto {
    private String id;
    private String situation;
    private Map<String, String> translations; // 언어코드 -> 번역문구
    private boolean ttsAvailable;
    private int usageCount;

    public PhraseCardDto(String id, String situation, Map<String, String> translations, boolean ttsAvailable) {
        this.id = id;
        this.situation = situation;
        this.translations = translations;
        this.ttsAvailable = ttsAvailable;
        this.usageCount = 0;
    }
}
