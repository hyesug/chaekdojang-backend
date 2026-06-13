package com.chaekingam.api.domain.admin.dto;

import com.chaekingam.api.domain.review.Review;

import java.time.LocalDateTime;

public record AdminReviewResponse(
        Long id,
        String authorNickname,
        String bookTitle,
        String content,
        int rating,
        boolean hidden,
        LocalDateTime createdAt
) {
    public static AdminReviewResponse from(Review r) {
        return new AdminReviewResponse(
                r.getId(),
                r.getAuthor().getNickname(),
                r.getBook() != null ? r.getBook().getTitle() : null,
                r.getContent(),
                r.getRating(),
                r.isHidden(),
                r.getCreatedAt()
        );
    }
}
