package com.chaekingam.api.domain.review;

import com.chaekingam.api.domain.book.Book;
import com.chaekingam.api.domain.book.BookRepository;
import com.chaekingam.api.domain.review.dto.ReviewCreateRequest;
import com.chaekingam.api.domain.review.dto.ReviewResponse;
import com.chaekingam.api.domain.review.dto.ReviewUpdateRequest;
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
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

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
                .author(author)
                .book(book)
                .content(request.content())
                .rating(request.rating())
                .build();
        return ReviewResponse.from(reviewRepository.save(review));
    }

    public List<ReviewResponse> getAll() {
        return reviewRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc()
                .stream()
                .map(ReviewResponse::from)
                .toList();
    }

    public ReviewResponse getOne(Long id) {
        return ReviewResponse.from(findActiveReview(id));
    }

    @Transactional
    public ReviewResponse update(Long id, ReviewUpdateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        Review review = findActiveReview(id);
        if (!review.isAuthor(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        review.update(request.content(), request.rating());
        return ReviewResponse.from(review);
    }

    @Transactional
    public void delete(Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        Review review = findActiveReview(id);
        if (!review.isAuthor(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        review.softDelete();
    }

    private Review findActiveReview(Long id) {
        return reviewRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
    }
}
