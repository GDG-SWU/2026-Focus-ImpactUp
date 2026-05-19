package com.example.focus.domain.user.dto;

public class OnboardResponseDto {

    private String accessToken;
    private int expiresIn;
    private UserProfileResponseDto userProfile;

    public OnboardResponseDto() {
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UserProfileResponseDto getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfileResponseDto userProfile) {
        this.userProfile = userProfile;
    }
}