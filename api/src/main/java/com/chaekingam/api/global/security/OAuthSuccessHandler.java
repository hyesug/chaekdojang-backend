package com.chaekingam.api.global.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * OAuth2 로그인이 성공하면 Spring Security가 이 핸들러를 호출한다.
 * JWT를 만들어서 프론트엔드 콜백 페이지로 리다이렉트한다.
 */
@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuthUserPrincipal principal = (OAuthUserPrincipal) authentication.getPrincipal();
        String token = jwtProvider.generate(principal.getUserId());
        response.sendRedirect(frontendUrl + "/auth/callback?token=" + token);
    }
}
