package com.chaekingam.api.domain.chat;

import com.chaekingam.api.domain.book.Book;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_rooms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", unique = true, nullable = false)
    private Book book;

    @Builder
    private ChatRoom(Book book) {
        this.book = book;
    }

    public static ChatRoom create(Book book) {
        return ChatRoom.builder().book(book).build();
    }
}
