package com.chaekdojang.api.domain.chat;

import com.chaekdojang.api.domain.chat.dto.ChatMessageRequest;
import com.chaekdojang.api.domain.chat.dto.ChatMessageResponse;
import com.chaekdojang.api.global.response.ApiResponse;
import com.chaekdojang.api.global.security.OAuthUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 이전 메시지 조회 (REST)
    @GetMapping("/api/chat/{bookId}/messages")
    public ApiResponse<List<ChatMessageResponse>> getMessages(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "0") int page) {
        return ApiResponse.ok(chatService.getMessages(bookId, page));
    }

    // 채팅방 입장 (채팅방 생성 또는 조회)
    @PostMapping("/api/chat/{bookId}/join")
    public ApiResponse<Long> joinRoom(@PathVariable Long bookId) {
        ChatRoom room = chatService.getOrCreateRoom(bookId);
        return ApiResponse.ok(room.getId());
    }

    // WebSocket 메시지 발송 (/app/chat/{bookId} 로 전송)
    @MessageMapping("/chat/{bookId}")
    public void sendMessage(
            @DestinationVariable Long bookId,
            ChatMessageRequest request,
            Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        chatService.sendMessage(bookId, userId, request);
    }
}
