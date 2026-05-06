package com.chaekingam.api.domain.review;

import com.chaekingam.api.domain.user.User;
import com.chaekingam.api.domain.user.UserRepository;
import com.chaekingam.api.global.exception.CustomException;
import com.chaekingam.api.global.exception.ErrorCode;
import com.chaekingam.api.global.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewLikeService {

    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Transactional
    public void like(Long reviewId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId)) {
            throw new CustomException(ErrorCode.LIKE_ALREADY_EXISTS);
        }
        Review review = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        reviewLikeRepository.save(ReviewLike.builder().review(review).user(user).build());
    }

    @Transactional
    public void unlike(Long reviewId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (!reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId)) {
            throw new CustomException(ErrorCode.LIKE_NOT_FOUND);
        }
        reviewLikeRepository.deleteByReviewIdAndUserId(reviewId, userId);
    }

    public long countLikes(Long reviewId) {
        return reviewLikeRepository.countByReviewId(reviewId);
    }

    public boolean isLiked(Long reviewId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId);
    }
}
