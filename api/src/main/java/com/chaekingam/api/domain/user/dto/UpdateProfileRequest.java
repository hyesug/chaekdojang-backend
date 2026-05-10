package com.chaekingam.api.domain.user.dto;

public record UpdateProfileRequest(
        String nickname,
        String bio,
        String profileImage
) {}
