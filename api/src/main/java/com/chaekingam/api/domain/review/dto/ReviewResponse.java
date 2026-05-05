package com.chaekingam.api.domain.review.dto;

import com.chaekingam.api.domain.book.Book;
import com.chaekingam.api.domain.review.Review;
import com.chaekingam.api.domain.user.User;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        AuthorInfo author,
        BookInfo book,
        String content,
        int rating,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public record AuthorInfo(Long id, String nickname, String profileImage) {
        public static AuthorInfo from(User user) {
            return new AuthorInfo(user.getId(), user.getNickname(), user.getProfileImage());
        }
    }

    public record BookInfo(Long id, String isbn13, String title, String author, String thumbnail) {
        public static BookInfo from(Book book) {
            return new BookInfo(book.getId(), book.getIsbn13(), book.getTitle(),
                    book.getAuthor(), book.getThumbnail());
        }
    }

    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getId(),
                AuthorInfo.from(review.getAuthor()),
                review.getBook() != null ? BookInfo.from(review.getBook()) : null,
                review.getContent(),
                review.getRating(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
