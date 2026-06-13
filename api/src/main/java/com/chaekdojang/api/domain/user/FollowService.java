package com.chaekdojang.api.domain.user;

import com.chaekdojang.api.domain.notification.NotificationService;
import com.chaekdojang.api.domain.notification.NotificationType;
import com.chaekdojang.api.domain.user.dto.UserSummary;
import com.chaekdojang.api.global.exception.CustomException;
import com.chaekdojang.api.global.exception.ErrorCode;
import com.chaekdojang.api.global.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public void follow(Long targetUserId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId.equals(targetUserId)) {
            throw new CustomException(ErrorCode.CANNOT_FOLLOW_SELF);
        }
        if (followRepository.existsByFollowerIdAndFollowingId(currentUserId, targetUserId)) {
            throw new CustomException(ErrorCode.FOLLOW_ALREADY_EXISTS);
        }
        User follower = findUser(currentUserId);
        User following = findUser(targetUserId);
        followRepository.save(Follow.builder().follower(follower).following(following).build());
        notificationService.send(following, follower, NotificationType.FOLLOW, null);
    }

    @Transactional
    public void unfollow(Long targetUserId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!followRepository.existsByFollowerIdAndFollowingId(currentUserId, targetUserId)) {
            throw new CustomException(ErrorCode.FOLLOW_NOT_FOUND);
        }
        followRepository.deleteByFollowerIdAndFollowingId(currentUserId, targetUserId);
    }

    public List<UserSummary> getFollowers(Long userId) {
        return followRepository.findAllByFollowingIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(f -> UserSummary.from(f.getFollower()))
                .toList();
    }

    public List<UserSummary> getFollowings(Long userId) {
        return followRepository.findAllByFollowerIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(f -> UserSummary.from(f.getFollowing()))
                .toList();
    }

    public boolean isFollowing(Long targetUserId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return followRepository.existsByFollowerIdAndFollowingId(currentUserId, targetUserId);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
