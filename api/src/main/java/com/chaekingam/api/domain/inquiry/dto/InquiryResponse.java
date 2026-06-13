package com.chaekingam.api.domain.inquiry.dto;

import com.chaekingam.api.domain.inquiry.Inquiry;
import com.chaekingam.api.domain.inquiry.InquiryComment;

import java.time.LocalDateTime;
import java.util.List;

public record InquiryResponse(
        Long id,
        String title,
        String content,
        String authorName,
        LocalDateTime createdAt,
        List<CommentResponse> comments
) {
    public record CommentResponse(
            Long id,
            String authorName,
            String content,
            LocalDateTime createdAt
    ) {
        public static CommentResponse from(InquiryComment c) {
            return new CommentResponse(
                    c.getId(),
                    c.getAuthor().getNickname(),
                    c.getContent(),
                    c.getCreatedAt()
            );
        }
    }

    public static InquiryResponse from(Inquiry i) {
        return new InquiryResponse(
                i.getId(),
                i.getTitle(),
                i.getContent(),
                i.getAuthorName(),
                i.getCreatedAt(),
                i.getComments().stream().map(CommentResponse::from).toList()
        );
    }

    public static InquiryResponse summary(Inquiry i) {
        return new InquiryResponse(
                i.getId(),
                i.getTitle(),
                null,
                i.getAuthorName(),
                i.getCreatedAt(),
                List.of()
        );
    }
}
