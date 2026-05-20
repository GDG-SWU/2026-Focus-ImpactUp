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
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final HealthProfileRepository healthProfileRepository;

    private final Key jwtSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 토큰 만료 시간
    private final long accessTokenValidityInMilliseconds = 7200000;

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

            HealthProfile healthProfile = new HealthProfile(savedUser, requestDto.getHealthInfo().getBloodType());
            savedUser.setHealthProfile(healthProfile);

            if (requestDto.getHealthInfo().getConditions() != null) {
                healthProfile.getConditions().addAll(requestDto.getHealthInfo().getConditions());
            }
            if (requestDto.getHealthInfo().getAllergies() != null) {
                healthProfile.getAllergies().addAll(requestDto.getHealthInfo().getAllergies());
            }

            userRepository.save(savedUser);
        }

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.accessTokenValidityInMilliseconds);

        String userIdStr = savedUser.getId();

        String realAccessToken = Jwts.builder()
                .setSubject(userIdStr)
                .claim("role", "ROLE_USER")
                .setIssuedAt(new Date())
                .setExpiration(validity)
                .signWith(jwtSecretKey, SignatureAlgorithm.HS256)
                .compact();

        UserProfileResponseDto profileDto = new UserProfileResponseDto(savedUser, false);

        OnboardResponseDto responseDto = new OnboardResponseDto();
        responseDto.setAccessToken(realAccessToken);
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

        if (requestDto.getHealthInfo() != null) {
            HealthProfile healthProfile = user.getHealthProfile();

            // 혹시 온보딩 때 건강 정보를 입력 안 했던 올드 유저라면 새 프로필 생성 분기 처리
            if (healthProfile == null) {
                healthProfile = new HealthProfile(user, requestDto.getHealthInfo().getBloodType());
                user.setHealthProfile(healthProfile);
            } else {
                // 기존 프로필이 있다면 혈액형 단일 변수를 비즈니스 메서드로 안전하게 수정
                healthProfile.updateBloodType(requestDto.getHealthInfo().getBloodType());
            }

            // 기저질환 리스트 컬렉션 초기화 및 최신화
            if (requestDto.getHealthInfo().getConditions() != null) {
                healthProfile.getConditions().clear();
                healthProfile.getConditions().addAll(requestDto.getHealthInfo().getConditions());
            }

            // 알레르기 리스트 컬렉션 초기화 및 최신화
            if (requestDto.getHealthInfo().getAllergies() != null) {
                healthProfile.getAllergies().clear();
                healthProfile.getAllergies().addAll(requestDto.getHealthInfo().getAllergies());
            }

            userRepository.save(user); // 더티 체킹이 작동하지만 안전하게 영속성 명시
        }

        return new UserProfileResponseDto(user, false);
    }
}
