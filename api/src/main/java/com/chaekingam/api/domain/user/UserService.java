package com.chaekingam.api.domain.user;

import com.chaekingam.api.domain.library.LibraryRepository;
import com.chaekingam.api.domain.review.ReviewRepository;
import com.chaekingam.api.domain.user.dto.*;
import com.chaekingam.api.global.exception.CustomException;
import com.chaekingam.api.global.exception.ErrorCode;
import com.chaekingam.api.global.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final ReviewRepository reviewRepository;
    private final LibraryRepository libraryRepository;

    public UserProfileResponse getMyProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        return buildProfile(userId);
    }

    @Transactional
    public void deleteMe() {
        Long userId = SecurityUtils.getCurrentUserId();
        findUser(userId).softDelete();
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

    public List<UserRecommendationResponse> getRecommendations() {
        Long myId = SecurityUtils.getCurrentUserId();

        List<Long> myBookIds = libraryRepository.findBookIdsByUserId(myId);
        if (myBookIds.isEmpty()) {
            return List.of();
        }

        List<Long> followingIds = followRepository.findFollowingIdsByFollowerId(myId);
        List<Long> excludeUserIds = new ArrayList<>(followingIds);
        excludeUserIds.add(myId);

        List<Object[]> results = libraryRepository.findUsersWithMostBookOverlap(myBookIds, excludeUserIds);

        return results.stream()
                .limit(5)
                .map(row -> {
                    Long userId = ((Number) row[0]).longValue();
                    int overlapCount = ((Number) row[1]).intValue();
                    return userRepository.findById(userId)
                            .map(user -> UserRecommendationResponse.from(user, overlapCount))
                            .orElse(null);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public ReadingStatsResponse getReadingStats() {
        Long myId = SecurityUtils.getCurrentUserId();

        List<Object[]> monthlyData = libraryRepository.findMonthlyReadingStats(myId);
        List<ReadingStatsResponse.MonthlyCount> monthly = monthlyData.stream()
                .map(row -> new ReadingStatsResponse.MonthlyCount(
                        ((Number) row[0]).intValue(),
                        ((Number) row[1]).intValue(),
                        ((Number) row[2]).intValue()
                ))
                .toList();

        List<Object[]> genreData = libraryRepository.findGenreStats(myId);
        List<ReadingStatsResponse.GenreCount> genres = genreData.stream()
                .map(row -> new ReadingStatsResponse.GenreCount(
                        (String) row[0],
                        ((Number) row[1]).intValue()
                ))
                .toList();

        int totalFinished = monthly.stream().mapToInt(ReadingStatsResponse.MonthlyCount::count).sum();

        return new ReadingStatsResponse(totalFinished, monthly, genres);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
