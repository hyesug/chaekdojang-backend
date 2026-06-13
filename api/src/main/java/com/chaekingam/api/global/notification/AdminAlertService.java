package com.chaekingam.api.global.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAlertService {

    private final JavaMailSender mailSender;

    @Value("${notification.admin-email}")
    private String adminEmail;

    public void sendSignupAlert(String userEmail, String nickname) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(adminEmail);
            message.setSubject("[책인감] 신규 가입");
            message.setText("닉네임: " + nickname + "\n이메일: " + (userEmail != null ? userEmail : "없음"));
            mailSender.send(message);
        } catch (Exception e) {
            log.warn("가입 알림 이메일 발송 실패: {}", e.getMessage());
        }
    }
}
