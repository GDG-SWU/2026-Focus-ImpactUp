package com.example.focus.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_allergies")
@Getter
@NoArgsConstructor
public class UserAllergy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_profile_id", nullable = false)
    private HealthProfile healthProfile;

    @Column(name = "allergy_code", nullable = false, length = 50)
    private String allergyCode; // 예: "penicillin"

    public UserAllergy(HealthProfile healthProfile, String allergyCode) {
        this.healthProfile = healthProfile;
        this.allergyCode = allergyCode;
    }
}