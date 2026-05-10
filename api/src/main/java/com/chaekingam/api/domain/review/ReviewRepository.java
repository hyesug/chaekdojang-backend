package com.chaekingam.api.domain.review;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByDeletedAtIsNullOrderByCreatedAtDesc();

    Optional<Review> findByIdAndDeletedAtIsNull(Long id);

    List<Review> findAllByAuthorIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long authorId);

    long countByAuthorIdAndDeletedAtIsNull(Long authorId);

    List<Review> findAllByAuthorIdInAndDeletedAtIsNullOrderByCreatedAtDesc(List<Long> authorIds);
}
