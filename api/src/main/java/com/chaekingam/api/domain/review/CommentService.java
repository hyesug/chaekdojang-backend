package com.chaekingam.api.domain.review;

import com.chaekingam.api.domain.notification.NotificationService;
import com.chaekingam.api.domain.notification.NotificationType;
import com.chaekingam.api.domain.review.dto.CommentCreateRequest;
import com.chaekingam.api.domain.review.dto.CommentResponse;
import com.chaekingam.api.domain.user.User;
import com.chaekingam.api.domain.user.UserRepository;
import com.chaekingam.api.global.exception.CustomException;
import com.chaekingam.api.global.exception.ErrorCode;
import com.chaekingam.api.global.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public CommentResponse create(Long reviewId, CommentCreateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        Review review = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Comment comment = Comment.builder()
                .review(review)
                .author(author)
                .content(request.content())
                .build();
        Comment saved = commentRepository.save(comment);
        notificationService.send(review.getAuthor(), author, NotificationType.COMMENT, reviewId);
        return CommentResponse.from(saved);
    }

    public List<CommentResponse> getAll(Long reviewId) {
        return commentRepository.findAllByReviewIdAndDeletedAtIsNullOrderByCreatedAtAsc(reviewId)
                .stream()
                .map(CommentResponse::from)
                .toList();
    }

    @Transactional
    public void delete(Long reviewId, Long commentId) {
        Long userId = SecurityUtils.getCurrentUserId();
        Comment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        if (!comment.getReview().getId().equals(reviewId)) {
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
        }
        if (!comment.isAuthor(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        comment.softDelete();
    }
}
