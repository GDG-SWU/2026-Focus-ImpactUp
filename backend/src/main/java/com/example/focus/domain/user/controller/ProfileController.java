package com.example.focus.domain.user.controller;

import com.example.focus.domain.user.dto.OnboardRequestDto;
import com.example.focus.domain.user.dto.OnboardResponseDto;
import com.example.focus.domain.user.dto.ProfileUpdateRequestDto;
import com.example.focus.domain.user.entity.User;
import com.example.focus.domain.user.dto.UserProfileResponseDto;
import com.example.focus.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService; // 🌟 진짜 서비스 연결고리 장착!

    /**
     * 1. 사용자 통합 온보딩 (POST /api/v1/users/onboard)
     */
    @PostMapping("/onboard")
    public ResponseEntity<OnboardResponseDto> onboardUser(
            @Valid @RequestBody OnboardRequestDto requestDto) {

        OnboardResponseDto responseBody = userService.onboardUser(requestDto);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + responseBody.getAccessToken());

        return new ResponseEntity<>(responseBody, headers, HttpStatus.CREATED);
    }

    /**
     * 2. 프로필 전체 조회 (GET /api/v1/users/profile)
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponseDto> getUserProfile(@RequestHeader("X-User-Id") String userIdStr) {
        // 유저 반환
        UUID userId = UUID.fromString(userIdStr.trim());
        UserProfileResponseDto responseBody = userService.getUserProfile(userId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Offline-Cache", "false");

        return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
    }

    /**
     * 3. 프로필 정보 수정 (PATCH /api/v1/users/profile)
     */
    @PatchMapping("/profile")
    public ResponseEntity<UserProfileResponseDto> updateProfile(
            @RequestHeader("X-User-Id") String userIdStr,
            @RequestBody ProfileUpdateRequestDto requestDto) {

        UUID userId = UUID.fromString(userIdStr.trim());
        UserProfileResponseDto responseBody = userService.updateProfile(userId, requestDto);

        return ResponseEntity.ok(responseBody);
    }
}