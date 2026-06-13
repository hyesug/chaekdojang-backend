package com.chaekdojang.api.domain.review.dto;

import com.chaekdojang.api.domain.review.Comment;
import com.chaekdojang.api.domain.user.User;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        AuthorInfo author,
        String content,
        LocalDateTime createdAt
) {
    public record AuthorInfo(Long id, String nickname, String profileImage) {
        public static AuthorInfo from(User user) {
            return new AuthorInfo(user.getId(), user.getNickname(), user.getProfileImage());
        }
    }

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                AuthorInfo.from(comment.getAuthor()),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
