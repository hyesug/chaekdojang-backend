package com.chaekdojang.api.infra.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${app.notification.admin-email:}")
    private String adminEmail;

    @Async
    public void sendInquiryNotification(String authorName, String title) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) return;
        if (adminEmail == null || adminEmail.isBlank()) return;

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(adminEmail);
            msg.setSubject("[책도장] 새 문의가 접수되었습니다");
            msg.setText("작성자: " + authorName + "\n제목: " + title + "\n\n관리자 페이지에서 확인하세요.");
            mailSender.send(msg);
        } catch (Exception e) {
            log.warn("문의 알림 메일 발송 실패: {}", e.getMessage());
        }
    }
}
