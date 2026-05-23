package com.chaekingam.api.domain.chat.dto;

import com.chaekingam.api.domain.chat.ChatMessage;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long id,
        Long senderId,
        String senderNickname,
        String senderProfileImage,
        String content,
        LocalDateTime createdAt
) {
    public static ChatMessageResponse from(ChatMessage m) {
        return new ChatMessageResponse(
                m.getId(),
                m.getSender().getId(),
                m.getSender().getNickname(),
                m.getSender().getProfileImage(),
                m.getContent(),
                m.getCreatedAt()
        );
    }
}
