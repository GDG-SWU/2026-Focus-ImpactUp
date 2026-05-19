package com.example.focus.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class HealthProfileRequestDto {

    private List<String> conditions = new ArrayList<>();
    private List<String> allergies = new ArrayList<>();
}