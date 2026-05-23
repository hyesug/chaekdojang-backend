package com.chaekingam.api.domain.chat;

import com.chaekingam.api.domain.book.Book;
import com.chaekingam.api.domain.book.BookRepository;
import com.chaekingam.api.domain.chat.dto.ChatMessageRequest;
import com.chaekingam.api.domain.chat.dto.ChatMessageResponse;
import com.chaekingam.api.domain.user.User;
import com.chaekingam.api.domain.user.UserRepository;
import com.chaekingam.api.global.exception.CustomException;
import com.chaekingam.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatRoom getOrCreateRoom(Long bookId) {
        return chatRoomRepository.findByBookId(bookId).orElseGet(() -> {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new CustomException(ErrorCode.BOOK_NOT_FOUND));
            return chatRoomRepository.save(ChatRoom.create(book));
        });
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessages(Long bookId, int page) {
        ChatRoom room = chatRoomRepository.findByBookId(bookId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        List<ChatMessage> messages = chatMessageRepository.findByRoomIdDesc(
                room.getId(), PageRequest.of(page, 50));
        return messages.reversed().stream().map(ChatMessageResponse::from).toList();
    }

    public ChatMessageResponse sendMessage(Long bookId, Long userId, ChatMessageRequest req) {
        ChatRoom room = getOrCreateRoom(bookId);
        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ChatMessage message = chatMessageRepository.save(
                ChatMessage.builder()
                        .chatRoom(room)
                        .sender(sender)
                        .content(req.content())
                        .build()
        );

        ChatMessageResponse response = ChatMessageResponse.from(message);
        messagingTemplate.convertAndSend("/topic/chat/" + bookId, response);
        return response;
    }
}
