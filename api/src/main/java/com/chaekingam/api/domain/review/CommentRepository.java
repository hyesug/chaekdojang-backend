package com.chaekingam.api.domain.review;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByReviewIdAndDeletedAtIsNullOrderByCreatedAtAsc(Long reviewId);

    Optional<Comment> findByIdAndDeletedAtIsNull(Long id);

    long countByReviewIdAndDeletedAtIsNull(Long reviewId);
}
