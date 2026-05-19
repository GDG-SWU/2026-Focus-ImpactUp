package com.example.focus.domain.user.service.impl;

import com.example.focus.domain.user.dto.ProfileUpdateRequestDto;
import com.example.focus.domain.user.entity.User;
import com.example.focus.domain.user.entity.HealthProfile;
import com.example.focus.domain.user.repository.UserRepository;
import com.example.focus.domain.user.repository.HealthProfileRepository;
import com.example.focus.domain.user.service.UserService;
import com.example.focus.domain.user.dto.OnboardRequestDto;
import com.example.focus.domain.user.dto.OnboardResponseDto;
import com.example.focus.domain.user.dto.UserProfileResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final HealthProfileRepository healthProfileRepository;

    public UserServiceImpl(UserRepository userRepository, HealthProfileRepository healthProfileRepository) {
        this.userRepository = userRepository;
        this.healthProfileRepository = healthProfileRepository;
    }

    @Override
    @Transactional
    public OnboardResponseDto onboardUser(OnboardRequestDto requestDto) {
        // 엔티티 생성
        User user = new User();
        user.setLocale(requestDto.getLocale());
        user.setPreferredLanguage(requestDto.getPreferredLanguage());
        user.setOnboardingCompleted(true);

        // 구글 클라우드 DB에 저장
        User savedUser = userRepository.save(user);

        // 기저질환, 알레르기정보 연동
        if (requestDto.getHealthInfo() != null) {

            HealthProfile healthProfile = new HealthProfile(savedUser);
            savedUser.setHealthProfile(healthProfile);

            if (requestDto.getHealthInfo().getConditions() != null) {
                healthProfile.getConditions().addAll(requestDto.getHealthInfo().getConditions());
            }
            if (requestDto.getHealthInfo().getAllergies() != null) {
                healthProfile.getAllergies().addAll(requestDto.getHealthInfo().getAllergies());
            }

            userRepository.save(savedUser);
        }

        UserProfileResponseDto profileDto = new UserProfileResponseDto(savedUser, false);

        OnboardResponseDto responseDto = new OnboardResponseDto();
        responseDto.setAccessToken("mock_access_token_xyz");
        responseDto.setUserProfile(profileDto);

        return responseDto;
    }

    @Override
    public UserProfileResponseDto getUserProfile(UUID userId) {
        User user = userRepository.findById(userId.toString())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. ID: " + userId));

        return new UserProfileResponseDto(user, false);
    }

    @Override
    @Transactional
    public UserProfileResponseDto updateProfile(UUID userId, ProfileUpdateRequestDto requestDto) {
        User user = userRepository.findById(userId.toString())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. ID: " + userId));

        if (requestDto.getPreferredLanguage() != null) {
            user.updatePreferredLanguage(requestDto.getPreferredLanguage());
        }

        return new UserProfileResponseDto(user, false);
    }
}
