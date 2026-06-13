package com.chaekingam.api.domain.user.dto;

import com.chaekingam.api.domain.user.User;

public record UserProfileResponse(
        Long id,
        String nickname,
        String bio,
        String profileImage,
        String role,
        long reviewCount,
        long followerCount,
        long followingCount
) {
    public static UserProfileResponse of(User user, long reviewCount, long followerCount, long followingCount) {
        return new UserProfileResponse(
                user.getId(),
                user.getNickname(),
                user.getBio(),
                user.getProfileImage(),
                user.getRole().name(),
                reviewCount,
                followerCount,
                followingCount
        );
    }
}
