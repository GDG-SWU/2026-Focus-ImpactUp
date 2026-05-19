package com.example.focus.domain.translation.dto;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PhrasebookResponseDto {
    private String category;
    private List<PhraseCardDto> cards;
    private boolean offline;

    public PhrasebookResponseDto(String category, List<PhraseCardDto> cards, boolean offline) {
        this.category = category;
        this.cards = cards;
        this.offline = offline;
    }
}
