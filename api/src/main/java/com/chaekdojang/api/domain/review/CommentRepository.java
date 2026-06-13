package com.chaekdojang.api.domain.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByReviewIdAndDeletedAtIsNullOrderByCreatedAtAsc(Long reviewId);

    Optional<Comment> findByIdAndDeletedAtIsNull(Long id);

    long countByReviewIdAndDeletedAtIsNull(Long reviewId);

    // 리뷰 ID 목록을 한 번의 쿼리로 카운트 — N+1 방지
    @Query("SELECT c.review.id, COUNT(c) FROM Comment c WHERE c.review.id IN :ids AND c.deletedAt IS NULL GROUP BY c.review.id")
    List<Object[]> countGroupByReviewIds(@Param("ids") List<Long> ids);
}
