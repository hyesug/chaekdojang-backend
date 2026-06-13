package com.chaekingam.api.domain.review;

import com.chaekingam.api.domain.admin.dto.BookReviewStatResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByDeletedAtIsNullOrderByCreatedAtDesc();

    Page<Review> findAllByDeletedAtIsNull(Pageable pageable);

    Optional<Review> findByIdAndDeletedAtIsNull(Long id);

    List<Review> findAllByAuthorIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long authorId);

    long countByAuthorIdAndDeletedAtIsNull(Long authorId);

    List<Review> findAllByAuthorIdInAndDeletedAtIsNullOrderByCreatedAtDesc(List<Long> authorIds);

    List<Review> findAllByBookIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long bookId);

    Page<Review> findAllByDeletedAtIsNullAndHiddenFalse(Pageable pageable);

    List<Review> findAllByAuthorIdInAndDeletedAtIsNullAndHiddenFalseOrderByCreatedAtDesc(List<Long> authorIds);

    List<Review> findAllByAuthorIdAndDeletedAtIsNullAndHiddenFalseOrderByCreatedAtDesc(Long authorId);

    List<Review> findAllByBookIdAndDeletedAtIsNullAndHiddenFalseOrderByCreatedAtDesc(Long bookId);

    @Query(value = "SELECT r FROM Review r WHERE r.deletedAt IS NULL AND r.hidden = false " +
                   "ORDER BY (SELECT COUNT(rl) FROM ReviewLike rl WHERE rl.review = r) DESC, r.createdAt DESC",
           countQuery = "SELECT COUNT(r) FROM Review r WHERE r.deletedAt IS NULL AND r.hidden = false")
    Page<Review> findAllByPopularity(Pageable pageable);

    @Query("SELECT new com.chaekingam.api.domain.admin.dto.BookReviewStatResponse(" +
           "b.id, b.title, b.author, COUNT(r)) " +
           "FROM Review r JOIN r.book b " +
           "WHERE r.deletedAt IS NULL " +
           "GROUP BY b.id, b.title, b.author " +
           "ORDER BY COUNT(r) DESC")
    List<BookReviewStatResponse> findBookReviewStats();
}
