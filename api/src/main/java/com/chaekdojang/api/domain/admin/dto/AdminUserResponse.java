package com.chaekdojang.api.domain.admin.dto;

import com.chaekdojang.api.domain.user.User;
import com.chaekdojang.api.domain.user.UserRole;

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
