package com.chaekingam.api.domain.admin.dto;

import com.chaekingam.api.domain.user.User;
import com.chaekingam.api.domain.user.UserRole;

import java.time.LocalDateTime;

public record AdminUserResponse(
        Long id,
        String nickname,
        String email,
        String profileImage,
        UserRole role,
        LocalDateTime createdAt
) {
    public static AdminUserResponse from(User u) {
        return new AdminUserResponse(u.getId(), u.getNickname(), u.getEmail(),
                u.getProfileImage(), u.getRole(), u.getCreatedAt());
    }
}
