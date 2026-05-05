package com.chaekingam.api.global.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetEmail(String to, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[책인감] 비밀번호 재설정 안내");
        message.setText("""
                안녕하세요, 책인감입니다.

                아래 링크를 클릭하여 비밀번호를 재설정해 주세요.
                링크는 1시간 후 만료됩니다.

                %s

                본인이 요청하지 않은 경우 이 메일을 무시해 주세요.
                """.formatted(resetLink));
        mailSender.send(message);
    }
}
