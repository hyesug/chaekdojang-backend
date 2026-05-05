package com.chaekingam.api.domain.user;

import com.chaekingam.api.domain.user.dto.AuthResponse;
import com.chaekingam.api.domain.user.dto.ForgotPasswordRequest;
import com.chaekingam.api.domain.user.dto.LoginRequest;
import com.chaekingam.api.domain.user.dto.RegisterRequest;
import com.chaekingam.api.domain.user.dto.ResetPasswordRequest;
import com.chaekingam.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증", description = "회원가입·로그인·비밀번호 찾기")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "이메일·비밀번호·닉네임으로 계정을 생성하고 JWT 토큰을 반환합니다.")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ApiResponse.ok(authService.register(request));
    }

    @Operation(summary = "로그인", description = "이메일·비밀번호로 로그인하고 JWT 토큰을 반환합니다.")
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @Operation(summary = "비밀번호 찾기 요청", description = "입력한 이메일로 비밀번호 재설정 링크를 발송합니다. 가입되지 않은 이메일도 동일한 응답을 반환합니다.")
    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ApiResponse.ok(null);
    }

    @Operation(summary = "비밀번호 재설정", description = "이메일로 받은 토큰과 새 비밀번호로 비밀번호를 변경합니다.")
    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.ok(null);
    }
}
