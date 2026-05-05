package com.chaekingam.api.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);

    List<Follow> findAllByFollowingIdOrderByCreatedAtDesc(Long followingId);

    List<Follow> findAllByFollowerIdOrderByCreatedAtDesc(Long followerId);

    long countByFollowingId(Long followingId);

    long countByFollowerId(Long followerId);
}
