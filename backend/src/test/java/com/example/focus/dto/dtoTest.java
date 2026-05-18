package com.example.focus.dto;

import com.example.focus.domain.HealthProfile;
import com.example.focus.domain.User;
import com.example.focus.domain.UserAllergy;
import com.example.focus.domain.UserCompanion;
import com.example.focus.domain.UserCondition;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DTO 단위 테스트
 * - OnboardRequestDto : @NotBlank / @NotNull / @Valid 체인 검증
 * - UserProfileResponseDto : User 엔티티 → DTO 변환 매핑 검증
 * - HealthProfileResponseDto : HealthProfile 엔티티 → DTO 변환 검증
 */
@DisplayName("DTO 단위 테스트")
class DtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ─────────────────────────────────────────────────────────
    // OnboardRequestDto 검증
    // ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("OnboardRequestDto Validation")
    class OnboardRequestDtoTest {

        private OnboardRequestDto validDto() {
            HealthProfileRequestDto health = new HealthProfileRequestDto();
            health.setConditions(List.of("diabetes"));
            health.setAllergies(List.of("penicillin"));

            OnboardRequestDto dto = new OnboardRequestDto();
            dto.setLocale("ar-SD");
            dto.setPreferredLanguage("ar");
            dto.setHealthInfo(health);
            dto.setCompanions(List.of("child"));
            return dto;
        }

        @Test
        @DisplayName("정상 DTO는 Violation 없음")
        void valid_dto_no_violations() {
            Set<ConstraintViolation<OnboardRequestDto>> violations =
                    validator.validate(validDto());

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("locale null → @NotBlank Violation 발생")
        void null_locale_causes_violation() {
            OnboardRequestDto dto = validDto();
            dto.setLocale(null);

            Set<ConstraintViolation<OnboardRequestDto>> violations = validator.validate(dto);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("locale"));
        }

        @Test
        @DisplayName("locale 공백 → @NotBlank Violation 발생")
        void blank_locale_causes_violation() {
            OnboardRequestDto dto = validDto();
            dto.setLocale("   ");

            Set<ConstraintViolation<OnboardRequestDto>> violations = validator.validate(dto);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("locale"));
        }

        @Test
        @DisplayName("preferredLanguage null → @NotBlank Violation 발생")
        void null_preferredLanguage_causes_violation() {
            OnboardRequestDto dto = validDto();
            dto.setPreferredLanguage(null);

            Set<ConstraintViolation<OnboardRequestDto>> violations = validator.validate(dto);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(
                    v -> v.getPropertyPath().toString().equals("preferredLanguage"));
        }

        @Test
        @DisplayName("healthInfo null → @NotNull Violation 발생")
        void null_healthInfo_causes_violation() {
            OnboardRequestDto dto = validDto();
            dto.setHealthInfo(null);

            Set<ConstraintViolation<OnboardRequestDto>> violations = validator.validate(dto);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(
                    v -> v.getPropertyPath().toString().equals("healthInfo"));
        }

        @Test
        @DisplayName("companions null이어도 Violation 없음 (선택 필드)")
        void null_companions_no_violation() {
            OnboardRequestDto dto = validDto();
            dto.setCompanions(null);

            Set<ConstraintViolation<OnboardRequestDto>> violations = validator.validate(dto);

            assertThat(violations).isEmpty();
        }
    }

    // ─────────────────────────────────────────────────────────
    // HealthProfileResponseDto 매핑 검증
    // ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("HealthProfileResponseDto 매핑")
    class HealthProfileResponseDtoTest {

        private User buildUser() {
            return User.builder()
                    .locale("ar-SD").preferredLanguage("ar").onboardingCompleted(true).build();
        }

        @Test
        @DisplayName("conditions / allergies 목록 정상 매핑")
        void healthProfile_conditions_allergies_mapped() {
            User user = buildUser();
            HealthProfile profile = new HealthProfile(user);
            profile.getConditions().add(new UserCondition(profile, "diabetes"));
            profile.getConditions().add(new UserCondition(profile, "hypertension"));
            profile.getAllergies().add(new UserAllergy(profile, "penicillin"));

            HealthProfileResponseDto dto = new HealthProfileResponseDto(profile);

            assertThat(dto.getConditions()).containsExactlyInAnyOrder("diabetes", "hypertension");
            assertThat(dto.getAllergies()).containsExactly("penicillin");
        }

        @Test
        @DisplayName("빈 conditions / allergies 일 때 빈 리스트 반환")
        void empty_health_profile_returns_empty_lists() {
            User user = buildUser();
            HealthProfile profile = new HealthProfile(user);

            HealthProfileResponseDto dto = new HealthProfileResponseDto(profile);

            assertThat(dto.getConditions()).isEmpty();
            assertThat(dto.getAllergies()).isEmpty();
        }

        @Test
        @DisplayName("기본 생성자로 생성 시 빈 리스트 초기화")
        void default_constructor_initializes_empty_lists() {
            HealthProfileResponseDto dto = new HealthProfileResponseDto();

            assertThat(dto.getConditions()).isNotNull().isEmpty();
            assertThat(dto.getAllergies()).isNotNull().isEmpty();
        }
    }

    // ─────────────────────────────────────────────────────────
    // UserProfileResponseDto 매핑 검증
    // ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("UserProfileResponseDto 매핑")
    class UserProfileResponseDtoTest {

        private User buildUser() {
            return User.builder()
                    .locale("ar-SD").preferredLanguage("ar").onboardingCompleted(true).build();
        }

        @Test
        @DisplayName("User 기본 필드 정상 매핑")
        void basic_user_fields_mapped() {
            User user = buildUser();

            UserProfileResponseDto dto = new UserProfileResponseDto(user, false);

            assertThat(dto.getPreferredLanguage()).isEqualTo("ar");
            assertThat(dto.getLocale()).isEqualTo("ar-SD");
            assertThat(dto.isOnboardingCompleted()).isTrue();
            assertThat(dto.isOffline()).isFalse();
        }

        @Test
        @DisplayName("offline = true 플래그 정상 반영")
        void offline_flag_true_mapped() {
            User user = buildUser();

            UserProfileResponseDto dto = new UserProfileResponseDto(user, true);

            assertThat(dto.isOffline()).isTrue();
        }

        @Test
        @DisplayName("healthProfile 없을 때 빈 health DTO 반환 (NPE 방지)")
        void null_healthProfile_returns_empty_health_dto() {
            User user = buildUser(); // healthProfile = null

            UserProfileResponseDto dto = new UserProfileResponseDto(user, false);

            assertThat(dto.getHealth()).isNotNull();
            assertThat(dto.getHealth().getConditions()).isEmpty();
            assertThat(dto.getHealth().getAllergies()).isEmpty();
        }

        @Test
        @DisplayName("healthProfile 있을 때 conditions / allergies 정상 매핑")
        void healthProfile_present_maps_conditions_and_allergies() {
            User user = buildUser();
            HealthProfile profile = new HealthProfile(user);
            profile.getConditions().add(new UserCondition(profile, "diabetes"));
            profile.getAllergies().add(new UserAllergy(profile, "penicillin"));

            // User에 healthProfile 주입 불가(private setter 없음) → Reflection 사용
            try {
                var field = User.class.getDeclaredField("healthProfile");
                field.setAccessible(true);
                field.set(user, profile);
            } catch (Exception e) {
                throw new RuntimeException("Reflection 실패", e);
            }

            UserProfileResponseDto dto = new UserProfileResponseDto(user, false);

            assertThat(dto.getHealth().getConditions()).containsExactly("diabetes");
            assertThat(dto.getHealth().getAllergies()).containsExactly("penicillin");
        }

        @Test
        @DisplayName("companions 리스트 정상 매핑")
        void companions_list_mapped() {
            User user = buildUser();
            user.getCompanions().add(new UserCompanion(user, "child"));
            user.getCompanions().add(new UserCompanion(user, "pregnant"));

            UserProfileResponseDto dto = new UserProfileResponseDto(user, false);

            assertThat(dto.getCompanions()).containsExactlyInAnyOrder("child", "pregnant");
        }

        @Test
        @DisplayName("companions 없을 때 빈 리스트 반환")
        void no_companions_returns_empty_list() {
            User user = buildUser();

            UserProfileResponseDto dto = new UserProfileResponseDto(user, false);

            assertThat(dto.getCompanions()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("healthProfile 없을 때 updatedAt은 createdAt과 동일")
        void no_healthProfile_updatedAt_equals_createdAt() {
            User user = buildUser();
            // @PrePersist 미호출 → createdAt = null → updatedAt도 null
            UserProfileResponseDto dto = new UserProfileResponseDto(user, false);

            assertThat(dto.getUpdatedAt()).isEqualTo(dto.getCreatedAt());
        }
    }
}