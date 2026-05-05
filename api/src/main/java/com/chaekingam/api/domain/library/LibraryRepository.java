package com.chaekingam.api.domain.library;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LibraryRepository extends JpaRepository<Library, Long> {

    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    List<Library> findAllByUserIdOrderByUpdatedAtDesc(Long userId);

    List<Library> findAllByUserIdAndStatusOrderByUpdatedAtDesc(Long userId, LibraryStatus status);

    Optional<Library> findByIdAndUserId(Long id, Long userId);
}
