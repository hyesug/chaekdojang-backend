package com.chaekingam.api.domain.review;

import com.chaekingam.api.domain.book.Book;
import com.chaekingam.api.domain.book.BookRepository;
import com.chaekingam.api.domain.review.dto.ReviewCreateRequest;
import com.chaekingam.api.domain.review.dto.ReviewResponse;
import com.chaekingam.api.domain.review.dto.ReviewUpdateRequest;
import com.chaekingam.api.domain.user.FollowRepository;
import com.chaekingam.api.domain.user.User;
import com.chaekingam.api.domain.user.UserRepository;
import com.chaekingam.api.global.exception.CustomException;
import com.chaekingam.api.global.exception.ErrorCode;
import com.chaekingam.api.global.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final CommentRepository commentRepository;
    private final FollowRepository followRepository;

    @Transactional
    public ReviewResponse create(ReviewCreateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Book book = null;
        if (request.bookId() != null) {
            book = bookRepository.findById(request.bookId())
                    .orElseThrow(() -> new CustomException(ErrorCode.BOOK_NOT_FOUND));
        }
        Review review = Review.builder()
                .author(author).book(book)
                .content(request.content()).rating(request.rating())
                .build();
        return ReviewResponse.from(reviewRepository.save(review), 0L, 0L);
    }

    // 페이지네이션 기본: page=0, size=10 / sort: recent(최신순) | rating(별점순)
    public Page<ReviewResponse> getAll(int page, int size, String sort) {
        Sort order = "rating".equals(sort)
                ? Sort.by("rating").descending().and(Sort.by("createdAt").descending())
                : Sort.by("createdAt").descending();
        Page<Review> reviewPage = reviewRepository.findAllByDeletedAtIsNullAndHiddenFalse(
                PageRequest.of(page, size, order));
        return toResponsePage(reviewPage);
    }

    public ReviewResponse getOne(Long id) {
        Review review = findActiveReview(id);
        return ReviewResponse.from(review,
                reviewLikeRepository.countByReviewId(id),
                commentRepository.countByReviewIdAndDeletedAtIsNull(id));
    }

    @Transactional
    public ReviewResponse update(Long id, ReviewUpdateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        Review review = findActiveReview(id);
        if (!review.isAuthor(userId)) throw new CustomException(ErrorCode.FORBIDDEN);
        review.update(request.content(), request.rating());
        return ReviewResponse.from(review,
                reviewLikeRepository.countByReviewId(id),
                commentRepository.countByReviewIdAndDeletedAtIsNull(id));
    }

    @Transactional
    public void delete(Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        Review review = findActiveReview(id);
        if (!review.isAuthor(userId)) throw new CustomException(ErrorCode.FORBIDDEN);
        review.softDelete();
    }

    public List<ReviewResponse> getFeed() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> followingIds = followRepository.findFollowingIdsByFollowerId(userId);
        if (followingIds.isEmpty()) return List.of();
        return toResponseList(
                reviewRepository.findAllByAuthorIdInAndDeletedAtIsNullAndHiddenFalseOrderByCreatedAtDesc(followingIds));
    }

    public List<ReviewResponse> getByUser(Long userId) {
        return toResponseList(
                reviewRepository.findAllByAuthorIdAndDeletedAtIsNullAndHiddenFalseOrderByCreatedAtDesc(userId));
    }

    public List<ReviewResponse> getByBook(Long bookId) {
        return toResponseList(
                reviewRepository.findAllByBookIdAndDeletedAtIsNullAndHiddenFalseOrderByCreatedAtDesc(bookId));
    }

    // ── 내부 헬퍼 ─────────────────────────────────────────────────────────────

    private Page<ReviewResponse> toResponsePage(Page<Review> page) {
        List<Long> ids = page.stream().map(Review::getId).toList();
        Map<Long, Long> likeMap = buildLikeCountMap(ids);
        Map<Long, Long> commentMap = buildCommentCountMap(ids);
        return page.map(r -> ReviewResponse.from(r,
                likeMap.getOrDefault(r.getId(), 0L),
                commentMap.getOrDefault(r.getId(), 0L)));
    }

    private List<ReviewResponse> toResponseList(List<Review> reviews) {
        if (reviews.isEmpty()) return List.of();
        List<Long> ids = reviews.stream().map(Review::getId).toList();
        Map<Long, Long> likeMap = buildLikeCountMap(ids);
        Map<Long, Long> commentMap = buildCommentCountMap(ids);
        return reviews.stream()
                .map(r -> ReviewResponse.from(r,
                        likeMap.getOrDefault(r.getId(), 0L),
                        commentMap.getOrDefault(r.getId(), 0L)))
                .toList();
    }

    // 리뷰 ID 목록 → {reviewId: likeCount} 맵 (쿼리 1번)
    private Map<Long, Long> buildLikeCountMap(List<Long> ids) {
        return reviewLikeRepository.countGroupByReviewIds(ids).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
    }

    // 리뷰 ID 목록 → {reviewId: commentCount} 맵 (쿼리 1번)
    private Map<Long, Long> buildCommentCountMap(List<Long> ids) {
        return commentRepository.countGroupByReviewIds(ids).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
    }

    private Review findActiveReview(Long id) {
        return reviewRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
    }
}
