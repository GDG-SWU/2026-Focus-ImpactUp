package com.example.focus.controller;

import com.example.focus.domain.user.controller.ProfileController;
import com.example.focus.domain.user.dto.HealthProfileRequestDto;
import com.example.focus.domain.user.dto.OnboardRequestDto;
import com.example.focus.domain.user.dto.ProfileUpdateRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ProfileController 단위 테스트
 *
 * 현재 Controller는 Mock 데이터 반환 구조이므로
 * - 요청 유효성 검증 (Validation)
 * - 응답 구조 / 상태코드 / 헤더 검증
 * 에 초점을 맞춘 테스트입니다.
 *
 * 실제 Service / Repository 연동 후에는
 * @SpringBootTest + @AutoConfigureMockMvc 로 전환하거나
 * @MockitoBean 으로 Service를 주입해 사용하세요.
 */
@WebMvcTest(ProfileController.class)
@DisplayName("ProfileController 테스트")
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ─────────────────────────────────────────────────────────
    // 공통 픽스처 헬퍼
    // ─────────────────────────────────────────────────────────

    /** 정상 온보딩 요청 DTO 생성 */
    private OnboardRequestDto validOnboardRequest() {
        HealthProfileRequestDto health = new HealthProfileRequestDto();
        health.setConditions(List.of("diabetes", "hypertension"));
        health.setAllergies(List.of("penicillin"));

        OnboardRequestDto dto = new OnboardRequestDto();
        dto.setLocale("ar-SD");
        dto.setPreferredLanguage("ar");
        dto.setHealthInfo(health);
        dto.setCompanions(List.of("child", "pregnant"));
        return dto;
    }

    // ─────────────────────────────────────────────────────────
    // 1. POST /api/v1/users/onboard — 온보딩
    // ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/v1/users/onboard — 온보딩")
    class OnboardTest {

        @Test
        @DisplayName("정상 요청 시 201 Created 반환")
        void onboard_success_returns_201() throws Exception {
            mockMvc.perform(post("/api/v1/users/onboard")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validOnboardRequest())))
                    .andDo(print())
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("정상 요청 시 Authorization 헤더 포함")
        void onboard_success_has_authorization_header() throws Exception {
            mockMvc.perform(post("/api/v1/users/onboard")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validOnboardRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Authorization"));
        }

        @Test
        @DisplayName("응답 Body에 preferredLanguage 포함")
        void onboard_success_returns_preferredLanguage() throws Exception {
            mockMvc.perform(post("/api/v1/users/onboard")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validOnboardRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.preferredLanguage").value("ar"));
        }

        @Test
        @DisplayName("응답 Body에 locale 포함")
        void onboard_success_returns_locale() throws Exception {
            mockMvc.perform(post("/api/v1/users/onboard")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validOnboardRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.locale").value("ar-SD"));
        }

        @Test
        @DisplayName("응답 Body에 onboardingCompleted = true 포함")
        void onboard_success_onboardingCompleted_true() throws Exception {
            mockMvc.perform(post("/api/v1/users/onboard")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validOnboardRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.onboardingCompleted").value(true));
        }

        @Test
        @DisplayName("응답 Body에 offline = false 포함")
        void onboard_success_offline_false() throws Exception {
            mockMvc.perform(post("/api/v1/users/onboard")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validOnboardRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.offline").value(false));
        }

        // ── Validation 검증 ──────────────────────────────────

        @Test
        @DisplayName("locale 누락 시 400 Bad Request 반환")
        void onboard_missing_locale_returns_400() throws Exception {
            OnboardRequestDto dto = validOnboardRequest();
            dto.setLocale(null);

            mockMvc.perform(post("/api/v1/users/onboard")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("locale 빈 문자열 시 400 Bad Request 반환")
        void onboard_blank_locale_returns_400() throws Exception {
            OnboardRequestDto dto = validOnboardRequest();
            dto.setLocale("   ");

            mockMvc.perform(post("/api/v1/users/onboard")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("preferredLanguage 누락 시 400 Bad Request 반환")
        void onboard_missing_preferredLanguage_returns_400() throws Exception {
            OnboardRequestDto dto = validOnboardRequest();
            dto.setPreferredLanguage(null);

            mockMvc.perform(post("/api/v1/users/onboard")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("healthInfo 누락 시 400 Bad Request 반환")
        void onboard_missing_healthInfo_returns_400() throws Exception {
            OnboardRequestDto dto = validOnboardRequest();
            dto.setHealthInfo(null);

            mockMvc.perform(post("/api/v1/users/onboard")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("companions 없이 요청해도 201 반환 (선택 필드)")
        void onboard_without_companions_returns_201() throws Exception {
            OnboardRequestDto dto = validOnboardRequest();
            dto.setCompanions(List.of()); // 빈 리스트

            mockMvc.perform(post("/api/v1/users/onboard")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("conditions, allergies 빈 리스트로 요청해도 201 반환")
        void onboard_empty_health_lists_returns_201() throws Exception {
            HealthProfileRequestDto emptyHealth = new HealthProfileRequestDto();
            emptyHealth.setConditions(List.of());
            emptyHealth.setAllergies(List.of());

            OnboardRequestDto dto = validOnboardRequest();
            dto.setHealthInfo(emptyHealth);

            mockMvc.perform(post("/api/v1/users/onboard")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Body 없이 요청 시 400 Bad Request 반환")
        void onboard_no_body_returns_400() throws Exception {
            mockMvc.perform(post("/api/v1/users/onboard")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Content-Type 없이 요청 시 415 Unsupported Media Type 반환")
        void onboard_no_content_type_returns_415() throws Exception {
            mockMvc.perform(post("/api/v1/users/onboard")
                            .content(objectMapper.writeValueAsString(validOnboardRequest())))
                    .andExpect(status().isUnsupportedMediaType());
        }
    }

    // ─────────────────────────────────────────────────────────
    // 2. GET /api/v1/users/profile — 프로필 전체 조회
    // ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/v1/users/profile — 프로필 조회")
    class GetProfileTest {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void getProfile_returns_200() throws Exception {
            mockMvc.perform(get("/api/v1/users/profile"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("X-Offline-Cache 헤더 포함")
        void getProfile_has_offline_cache_header() throws Exception {
            mockMvc.perform(get("/api/v1/users/profile"))
                    .andExpect(status().isOk())
                    .andExpect(header().exists("X-Offline-Cache"))
                    .andExpect(header().string("X-Offline-Cache", "false"));
        }

        @Test
        @DisplayName("응답 Body에 userId 포함")
        void getProfile_returns_userId() throws Exception {
            mockMvc.perform(get("/api/v1/users/profile"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").exists());
        }

        @Test
        @DisplayName("응답 Body에 preferredLanguage 포함")
        void getProfile_returns_preferredLanguage() throws Exception {
            mockMvc.perform(get("/api/v1/users/profile"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.preferredLanguage").value("ar"));
        }

        @Test
        @DisplayName("응답 Body에 locale 포함")
        void getProfile_returns_locale() throws Exception {
            mockMvc.perform(get("/api/v1/users/profile"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.locale").value("ar-SD"));
        }

        @Test
        @DisplayName("응답 Body에 health 객체 포함")
        void getProfile_returns_health_object() throws Exception {
            mockMvc.perform(get("/api/v1/users/profile"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.health").exists())
                    .andExpect(jsonPath("$.health.conditions").isArray())
                    .andExpect(jsonPath("$.health.allergies").isArray());
        }

        @Test
        @DisplayName("응답 Body에 companions 배열 포함")
        void getProfile_returns_companions_array() throws Exception {
            mockMvc.perform(get("/api/v1/users/profile"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.companions").isArray());
        }

        @Test
        @DisplayName("응답 Body에 onboardingCompleted 포함")
        void getProfile_returns_onboardingCompleted() throws Exception {
            mockMvc.perform(get("/api/v1/users/profile"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.onboardingCompleted").value(true));
        }

        @Test
        @DisplayName("응답 Body에 offline 필드 포함")
        void getProfile_returns_offline_flag() throws Exception {
            mockMvc.perform(get("/api/v1/users/profile"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.offline").value(false));
        }

        @Test
        @DisplayName("응답 Body에 createdAt 포함")
        void getProfile_returns_createdAt() throws Exception {
            mockMvc.perform(get("/api/v1/users/profile"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.createdAt").exists());
        }
    }

    // ─────────────────────────────────────────────────────────
    // 3. PATCH /api/v1/users/profile — 프로필 수정
    // ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("PATCH /api/v1/users/profile — 프로필 수정")
    class UpdateProfileTest {

        @Test
        @DisplayName("언어만 수정 요청 시 200 OK 반환")
        void updateProfile_language_only_returns_200() throws Exception {
            ProfileUpdateRequestDto dto = new ProfileUpdateRequestDto();
            dto.setPreferredLanguage("fr");

            mockMvc.perform(patch("/api/v1/users/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("언어 수정 시 응답 Body의 preferredLanguage 반영")
        void updateProfile_language_reflected_in_response() throws Exception {
            ProfileUpdateRequestDto dto = new ProfileUpdateRequestDto();
            dto.setPreferredLanguage("fr");

            mockMvc.perform(patch("/api/v1/users/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.preferredLanguage").value("fr"));
        }

        @Test
        @DisplayName("preferredLanguage null 요청 시 기본값 ar 반환")
        void updateProfile_null_language_returns_default() throws Exception {
            ProfileUpdateRequestDto dto = new ProfileUpdateRequestDto();
            dto.setPreferredLanguage(null);

            mockMvc.perform(patch("/api/v1/users/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.preferredLanguage").value("ar")); // 컨트롤러 기본값
        }

        @Test
        @DisplayName("healthInfo 포함 수정 요청 시 200 OK 반환")
        void updateProfile_with_healthInfo_returns_200() throws Exception {
            HealthProfileRequestDto health = new HealthProfileRequestDto();
            health.setConditions(List.of("hypertension"));
            health.setAllergies(List.of("sulfonamide"));

            ProfileUpdateRequestDto dto = new ProfileUpdateRequestDto();
            dto.setPreferredLanguage("ar");
            dto.setHealthInfo(health);

            mockMvc.perform(patch("/api/v1/users/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("companions 포함 수정 요청 시 200 OK 반환")
        void updateProfile_with_companions_returns_200() throws Exception {
            ProfileUpdateRequestDto dto = new ProfileUpdateRequestDto();
            dto.setCompanions(List.of("elderly", "disabled"));

            mockMvc.perform(patch("/api/v1/users/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("빈 Body 요청 시 200 OK 반환 (PATCH는 전체 필드 불필요)")
        void updateProfile_empty_body_returns_200() throws Exception {
            mockMvc.perform(patch("/api/v1/users/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isOk());
        }
    }
}