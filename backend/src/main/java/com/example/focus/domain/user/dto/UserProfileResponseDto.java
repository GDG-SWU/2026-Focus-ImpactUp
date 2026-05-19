package com.example.focus.domain.user.dto;

import com.example.focus.domain.user.entity.User;
import com.example.focus.domain.user.entity.UserCompanion;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
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
    private final boolean offline;

    public UserProfileResponseDto(User user, boolean isOffline) {
         this.userId = user.getId();
        this.preferredLanguage = user.getPreferredLanguage();
        this.locale = user.getLocale();
        this.onboardingCompleted = user.isOnboardingCompleted();
        this.createdAt = user.getCreatedAt();

        if (user.getHealthProfile() != null) {
            this.health = new HealthProfileResponseDto(user.getHealthProfile());
            this.updatedAt = user.getHealthProfile().getUpdatedAt();
        } else {
            this.health = new HealthProfileResponseDto();
            this.updatedAt = user.getCreatedAt();
        }

        this.companions = user.getCompanions().stream()
                .map(UserCompanion::getCompanionType)
                .collect(Collectors.toList());

        this.offline = isOffline;
    }
}