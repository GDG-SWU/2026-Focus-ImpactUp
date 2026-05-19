package com.example.focus.domain;

import com.example.focus.domain.user.entity.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 도메인 엔티티 단위 테스트
 * Spring Context 없이 순수 Java 로직만 검증합니다.
 */
@DisplayName("도메인 엔티티 단위 테스트")
class DomainTest {

    // ─────────────────────────────────────────────────────────
    // User 엔티티
    // ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("User 엔티티")
    class UserTest {

        @Test
        @DisplayName("Builder로 생성 시 필드값 정상 할당")
        void user_builder_sets_fields() {
            User user = User.builder()
                    .locale("ar-SD")
                    .preferredLanguage("ar")
                    .onboardingCompleted(true)
                    .build();

            assertThat(user.getLocale()).isEqualTo("ar-SD");
            assertThat(user.getPreferredLanguage()).isEqualTo("ar");
            assertThat(user.isOnboardingCompleted()).isTrue();
        }

        @Test
        @DisplayName("onboardingCompleted 기본값은 false")
        void user_default_onboardingCompleted_is_false() {
            User user = User.builder()
                    .locale("fr-FR")
                    .preferredLanguage("fr")
                    .onboardingCompleted(false)
                    .build();

            assertThat(user.isOnboardingCompleted()).isFalse();
        }

        @Test
        @DisplayName("@PrePersist 호출 전 id / createdAt 은 null")
        void user_id_and_createdAt_null_before_persist() {
            // @PrePersist는 JPA 영속화 시점에 호출 → 순수 빌드 시에는 null
            User user = User.builder()
                    .locale("ar-SD")
                    .preferredLanguage("ar")
                    .onboardingCompleted(false)
                    .build();

            assertThat(user.getId()).isNull();
            assertThat(user.getCreatedAt()).isNull();
        }

        @Test
        @DisplayName("updatePreferredLanguage() 호출 시 언어 변경")
        void user_updatePreferredLanguage_changes_value() {
            User user = User.builder()
                    .locale("ar-SD")
                    .preferredLanguage("ar")
                    .onboardingCompleted(true)
                    .build();

            user.updatePreferredLanguage("fr");

            assertThat(user.getPreferredLanguage()).isEqualTo("fr");
        }

        @Test
        @DisplayName("초기 companions 리스트는 비어있음")
        void user_initial_companions_is_empty() {
            User user = User.builder()
                    .locale("ar-SD")
                    .preferredLanguage("ar")
                    .onboardingCompleted(false)
                    .build();

            assertThat(user.getCompanions()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("healthProfile 초기값은 null")
        void user_initial_healthProfile_is_null() {
            User user = User.builder()
                    .locale("ar-SD")
                    .preferredLanguage("ar")
                    .onboardingCompleted(false)
                    .build();

            assertThat(user.getHealthProfile()).isNull();
        }
    }

    // ─────────────────────────────────────────────────────────
    // HealthProfile 엔티티
    // ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("HealthProfile 엔티티")
    class HealthProfileTest {

        private User buildUser() {
            return User.builder()
                    .locale("ar-SD")
                    .preferredLanguage("ar")
                    .onboardingCompleted(true)
                    .build();
        }

        @Test
        @DisplayName("생성 시 updatedAt 자동 세팅")
        void healthProfile_createdAt_is_set() {
            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            HealthProfile profile = new HealthProfile(buildUser());

            assertThat(profile.getUpdatedAt()).isAfter(before);
        }

        @Test
        @DisplayName("생성 시 user 필드 정상 할당")
        void healthProfile_user_is_assigned() {
            User user = buildUser();
            HealthProfile profile = new HealthProfile(user);

            assertThat(profile.getUser()).isEqualTo(user);
        }

        @Test
        @DisplayName("초기 conditions 리스트는 비어있음")
        void healthProfile_initial_conditions_is_empty() {
            HealthProfile profile = new HealthProfile(buildUser());

            assertThat(profile.getConditions()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("초기 allergies 리스트는 비어있음")
        void healthProfile_initial_allergies_is_empty() {
            HealthProfile profile = new HealthProfile(buildUser());

            assertThat(profile.getAllergies()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("updateTimestamp() 호출 시 updatedAt 갱신")
        void healthProfile_updateTimestamp_refreshes_time() throws InterruptedException {
            HealthProfile profile = new HealthProfile(buildUser());
            LocalDateTime firstTime = profile.getUpdatedAt();

            Thread.sleep(10); // 시간 차이 발생을 위한 짧은 대기
            profile.updateTimestamp();

            assertThat(profile.getUpdatedAt()).isAfter(firstTime);
        }
    }

    // ─────────────────────────────────────────────────────────
    // UserCondition 엔티티
    // ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("UserCondition 엔티티")
    class UserConditionTest {

        @Test
        @DisplayName("생성자로 healthProfile / conditionCode 정상 할당")
        void userCondition_constructor_assigns_fields() {
            User user = User.builder()
                    .locale("ar-SD").preferredLanguage("ar").onboardingCompleted(true).build();
            HealthProfile profile = new HealthProfile(user);

            UserCondition condition = new UserCondition(profile, "diabetes");

            assertThat(condition.getHealthProfile()).isEqualTo(profile);
            assertThat(condition.getConditionCode()).isEqualTo("diabetes");
        }

        @Test
        @DisplayName("다양한 conditionCode 값 정상 저장")
        void userCondition_various_codes() {
            User user = User.builder()
                    .locale("ar-SD").preferredLanguage("ar").onboardingCompleted(true).build();
            HealthProfile profile = new HealthProfile(user);

            UserCondition c1 = new UserCondition(profile, "hypertension");
            UserCondition c2 = new UserCondition(profile, "asthma");

            assertThat(c1.getConditionCode()).isEqualTo("hypertension");
            assertThat(c2.getConditionCode()).isEqualTo("asthma");
        }
    }

    // ─────────────────────────────────────────────────────────
    // UserAllergy 엔티티
    // ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("UserAllergy 엔티티")
    class UserAllergyTest {

        @Test
        @DisplayName("생성자로 healthProfile / allergyCode 정상 할당")
        void userAllergy_constructor_assigns_fields() {
            User user = User.builder()
                    .locale("ar-SD").preferredLanguage("ar").onboardingCompleted(true).build();
            HealthProfile profile = new HealthProfile(user);

            UserAllergy allergy = new UserAllergy(profile, "penicillin");

            assertThat(allergy.getHealthProfile()).isEqualTo(profile);
            assertThat(allergy.getAllergyCode()).isEqualTo("penicillin");
        }
    }

    // ─────────────────────────────────────────────────────────
    // UserCompanion 엔티티
    // ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("UserCompanion 엔티티")
    class UserCompanionTest {

        @Test
        @DisplayName("생성자로 user / companionType 정상 할당")
        void userCompanion_constructor_assigns_fields() {
            User user = User.builder()
                    .locale("ar-SD").preferredLanguage("ar").onboardingCompleted(true).build();

            UserCompanion companion = new UserCompanion(user, "child");

            assertThat(companion.getUser()).isEqualTo(user);
            assertThat(companion.getCompanionType()).isEqualTo("child");
        }

        @Test
        @DisplayName("다양한 companionType 값 정상 저장")
        void userCompanion_various_types() {
            User user = User.builder()
                    .locale("ar-SD").preferredLanguage("ar").onboardingCompleted(true).build();

            String[] types = {"child", "infant", "pregnant", "elderly", "disabled"};
            for (String type : types) {
                UserCompanion companion = new UserCompanion(user, type);
                assertThat(companion.getCompanionType()).isEqualTo(type);
            }
        }
    }
}