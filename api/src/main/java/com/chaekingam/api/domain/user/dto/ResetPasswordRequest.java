package com.chaekingam.api.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank String token,
        @NotBlank @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다") String newPassword
) {
}
