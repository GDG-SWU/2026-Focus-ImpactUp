package com.example.focus.controller;

import com.example.focus.domain.User;
import com.example.focus.dto.OnboardRequestDto;
import com.example.focus.dto.ProfileUpdateRequestDto;
import com.example.focus.dto.UserProfileResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class ProfileController {

    // Reflection 없이 id / createdAt을 세팅한 Mock User 생성
    private User buildMockUser(String locale, String preferredLanguage) {
        User user = User.builder()
                .locale(locale)
                .preferredLanguage(preferredLanguage)
                .onboardingCompleted(true)
                .build();

        try {
            var idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, UUID.randomUUID().toString());

            var createdAtField = User.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(user, LocalDateTime.now());
        } catch (Exception e) {
            throw new RuntimeException("Mock User 필드 주입 실패", e);
        }

        return user;
    }

    /**
     * 1. 사용자 통합 온보딩 (POST /api/v1/users/onboard)
     */
    @PostMapping("/onboard")
    public ResponseEntity<UserProfileResponseDto> onboardUser(
            @Valid @RequestBody OnboardRequestDto requestDto) {

        User mockUser = buildMockUser(
                requestDto.getLocale(),
                requestDto.getPreferredLanguage()
        );

        String mockJwtToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mockToken...";
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, mockJwtToken);

        UserProfileResponseDto responseBody = new UserProfileResponseDto(mockUser, false);
        return new ResponseEntity<>(responseBody, headers, HttpStatus.CREATED);
    }

    /**
     * 2. 프로필 전체 조회 (GET /api/v1/users/profile)
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponseDto> getUserProfile() {

        User mockUser = buildMockUser("ar-SD", "ar");

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Offline-Cache", "false");

        UserProfileResponseDto responseBody = new UserProfileResponseDto(mockUser, false);
        return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
    }

    /**
     * 3. 프로필 정보 수정 (PATCH /api/v1/users/profile)
     */
    @PatchMapping("/profile")
    public ResponseEntity<UserProfileResponseDto> updateProfile(
            @RequestBody ProfileUpdateRequestDto requestDto) {

        // 수정 요청에서 언어가 null이면 기존값(ar) 유지
        String language = requestDto.getPreferredLanguage() != null
                ? requestDto.getPreferredLanguage()
                : "ar";

        User mockUser = buildMockUser("ar-SD", language);

        UserProfileResponseDto responseBody = new UserProfileResponseDto(mockUser, false);
        return ResponseEntity.ok(responseBody);
    }
}