package com.chaekingam.api.domain.user;

import com.chaekingam.api.domain.user.dto.AuthResponse;
import com.chaekingam.api.domain.user.dto.ForgotPasswordRequest;
import com.chaekingam.api.domain.user.dto.LoginRequest;
import com.chaekingam.api.domain.user.dto.RegisterRequest;
import com.chaekingam.api.domain.user.dto.ResetPasswordRequest;
import com.chaekingam.api.global.email.EmailService;
import com.chaekingam.api.global.exception.CustomException;
import com.chaekingam.api.global.exception.ErrorCode;
import com.chaekingam.api.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final EmailService emailService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (userRepository.existsByNickname(request.nickname())) {
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }
        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .build();
        userRepository.save(user);
        return new AuthResponse(jwtProvider.generate(user.getId()));
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }
        return new AuthResponse(jwtProvider.generate(user.getId()));
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        // 가입된 이메일이 없어도 동일한 응답 반환 — 이메일 존재 여부 노출 방지
        userRepository.findByEmail(request.email()).ifPresent(user -> {
            resetTokenRepository.deleteByUser(user);

            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .user(user)
                    .token(token)
                    .expiresAt(LocalDateTime.now().plusHours(1))
                    .build();
            resetTokenRepository.save(resetToken);

            String resetLink = frontendUrl + "/reset-password?token=" + token;
            emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
        });
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = resetTokenRepository.findByToken(request.token())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_RESET_TOKEN));

        if (resetToken.isExpired() || resetToken.isUsed()) {
            throw new CustomException(ErrorCode.INVALID_RESET_TOKEN);
        }

        resetToken.getUser().changePassword(passwordEncoder.encode(request.newPassword()));
        resetToken.markUsed();
    }
}
