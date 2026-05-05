package com.chaekingam.api.domain.user.dto;

import com.chaekingam.api.domain.user.User;

public record UserSummary(
        Long id,
        String nickname,
        String profileImage
) {
    public static UserSummary from(User user) {
        return new UserSummary(user.getId(), user.getNickname(), user.getProfileImage());
    }
}
