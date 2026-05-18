package com.example.focus.dto;

import com.example.focus.domain.User;
import com.example.focus.domain.UserCompanion;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserProfileResponseDto {

    private final String userId;
    private final String preferredLanguage;
    private final String locale;
    private final HealthProfileResponseDto health;
    private final List<String> companions;
    private final boolean onboardingCompleted;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final boolean offline; // 네트워크 플래그 (DB 미저장, 명세서 대응용)

    public UserProfileResponseDto(User user, boolean isOffline) {
        this.userId = user.getId();
        this.preferredLanguage = user.getPreferredLanguage();
        this.locale = user.getLocale();
        this.onboardingCompleted = user.isOnboardingCompleted();
        this.createdAt = user.getCreatedAt();

        // 건강 정보가 있으면 매핑, 없으면 빈 배열 구조 반환 (NullPointerException 방지)
        if (user.getHealthProfile() != null) {
            this.health = new HealthProfileResponseDto(user.getHealthProfile());
            this.updatedAt = user.getHealthProfile().getUpdatedAt();
        } else {
            this.health = new HealthProfileResponseDto();
            this.updatedAt = user.getCreatedAt();
        }

        // List<UserCompanion> 엔티티 목록을 List<String> 문자열 목록으로 변환
        this.companions = user.getCompanions().stream()
                .map(UserCompanion::getCompanionType)
                .collect(Collectors.toList());

        this.offline = isOffline;
    }
}