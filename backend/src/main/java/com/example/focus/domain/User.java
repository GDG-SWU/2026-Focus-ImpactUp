package com.example.focus.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User {

    @Id
    @Column(length = 36)
    private String id; // UUID 체계 반영 (VARCHAR(36))

    @Column(nullable = false, length = 10)
    private String locale;

    @Column(name = "preferred_language", nullable = false, length = 5)
    private String preferredLanguage;

    @Column(name = "onboarding_completed", nullable = false)
    private boolean onboardingCompleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 1:1 관계 매핑 (사용자 삭제 시 건강 프로필도 함께 삭제)
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private HealthProfile healthProfile;

    // 1:N 관계 매핑 (동반자 정보 리스트)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCompanion> companions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public User(String locale, String preferredLanguage, boolean onboardingCompleted) {
        this.locale = locale;
        this.preferredLanguage = preferredLanguage;
        this.onboardingCompleted = onboardingCompleted;
    }

    // 프로필 정보 수정(PATCH)을 위한 비즈니스 메서드
    public void updatePreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    // 연관관계 편의 메서드 (프로필 생성/결합 시 양방향 관계 원자적 세팅)
    public void setHealthProfile(HealthProfile healthProfile) {
        this.healthProfile = healthProfile;
        if (healthProfile != null && healthProfile.getUser() != this) {
            healthProfile.setUser(this);
        }
    }
}