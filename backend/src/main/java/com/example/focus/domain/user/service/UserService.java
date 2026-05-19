package com.example.focus.domain.user.service;

import com.example.focus.domain.user.dto.OnboardRequestDto;
import com.example.focus.domain.user.dto.OnboardResponseDto;
import com.example.focus.domain.user.dto.ProfileUpdateRequestDto;
import com.example.focus.domain.user.dto.UserProfileResponseDto;
import java.util.UUID;

public interface UserService {
    // 최초 온보딩
    OnboardResponseDto onboardUser(OnboardRequestDto requestDto);

    // 유저 프로필 조회
    UserProfileResponseDto getUserProfile(UUID userId);

    // 프로필 수정
    UserProfileResponseDto updateProfile(UUID userId, ProfileUpdateRequestDto requestDto);
}
