package com.chaekingam.api.domain.user.dto;

import com.chaekingam.api.domain.user.User;

public record UserRecommendationResponse(
        Long id,
        String nickname,
        String profileImage,
        String bio,
        int overlapCount
) {
    public static UserRecommendationResponse from(User user, int overlapCount) {
        return new UserRecommendationResponse(
                user.getId(),
                user.getNickname(),
                user.getProfileImage(),
                user.getBio(),
                overlapCount
        );
    }
}
