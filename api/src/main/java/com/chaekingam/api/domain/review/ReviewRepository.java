package com.chaekingam.api.domain.review;

import com.chaekingam.api.domain.admin.dto.BookReviewStatResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // 나와 같은 책을 읽고 별점을 남긴 유저별 유사도 점수 반환
    // 별점 동일: 2점, 차이 1: 1점, 차이 2 이상: 0점
    @Query("""
            SELECT r2.author.id,
                   SUM(CASE WHEN ABS(r1.rating - r2.rating) = 0 THEN 2
                            WHEN ABS(r1.rating - r2.rating) = 1 THEN 1
                            ELSE 0 END)
            FROM Review r1
            JOIN Review r2 ON r1.book = r2.book
            WHERE r1.author.id = :myId
              AND r2.author.id NOT IN :excludeIds
              AND r1.deletedAt IS NULL AND r2.deletedAt IS NULL
            GROUP BY r2.author.id
            """)
    List<Object[]> findRatingSimilarity(@Param("myId") Long myId, @Param("excludeIds") List<Long> excludeIds);

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
