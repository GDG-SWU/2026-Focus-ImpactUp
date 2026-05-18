package com.example.focus.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class HealthProfileRequestDto {

    private List<String> conditions = new ArrayList<>(); // ex) ["diabetes", "hypertension"]
    private List<String> allergies = new ArrayList<>();   // ex) ["penicillin"]
}