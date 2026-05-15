package com.chaekingam.api.domain.notification.dto;

import com.chaekingam.api.domain.notification.Notification;
import com.chaekingam.api.domain.notification.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        NotificationType type,
        String senderNickname,
        String senderProfileImage,
        Long targetId,
        String message,
        boolean isRead,
        LocalDateTime createdAt
) {
    public static NotificationResponse from(Notification n) {
        String message = switch (n.getType()) {
            case LIKE    -> n.getSender().getNickname() + "님이 독후감에 좋아요를 눌렀어요";
            case COMMENT -> n.getSender().getNickname() + "님이 댓글을 달았어요";
            case FOLLOW  -> n.getSender().getNickname() + "님이 팔로우하기 시작했어요";
        };
        return new NotificationResponse(
                n.getId(),
                n.getType(),
                n.getSender().getNickname(),
                n.getSender().getProfileImage(),
                n.getTargetId(),
                message,
                n.isRead(),
                n.getCreatedAt()
        );
    }
}
