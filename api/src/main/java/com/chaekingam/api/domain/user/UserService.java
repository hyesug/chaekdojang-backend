package com.chaekingam.api.domain.user;

import com.chaekingam.api.domain.review.ReviewRepository;
import com.chaekingam.api.domain.user.dto.UpdateProfileRequest;
import com.chaekingam.api.domain.user.dto.UserProfileResponse;
import com.chaekingam.api.domain.user.dto.UserSummary;
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
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final ReviewRepository reviewRepository;

    public UserProfileResponse getMyProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        return buildProfile(userId);
    }

    @Transactional
    public UserProfileResponse updateMyProfile(UpdateProfileRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = findUser(userId);

        if (request.nickname() != null && !request.nickname().equals(user.getNickname())) {
            if (userRepository.existsByNickname(request.nickname())) {
                throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
            }
        }

        user.updateProfile(request.nickname(), request.bio(), request.profileImage());
        return buildProfile(userId);
    }

    public UserProfileResponse getUserProfile(Long userId) {
        return buildProfile(userId);
    }

    private UserProfileResponse buildProfile(Long userId) {
        User user = findUser(userId);
        long reviewCount = reviewRepository.countByAuthorIdAndDeletedAtIsNull(userId);
        long followerCount = followRepository.countByFollowingId(userId);
        long followingCount = followRepository.countByFollowerId(userId);
        return UserProfileResponse.of(user, reviewCount, followerCount, followingCount);
    }

    public List<UserSummary> searchUsers(String q) {
        return userRepository.findByNicknameContainingIgnoreCase(q)
                .stream()
                .map(UserSummary::from)
                .toList();
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
