package com.example.focus.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_conditions")
@Getter
@NoArgsConstructor
public class UserCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_profile_id", nullable = false)
    private HealthProfile healthProfile;

    @Column(name = "condition_code", nullable = false, length = 50)
    private String conditionCode; // 예: "diabetes", "hypertension"

    public UserCondition(HealthProfile healthProfile, String conditionCode) {
        this.healthProfile = healthProfile;
        this.conditionCode = conditionCode;
    }
}