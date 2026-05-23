package com.chaekingam.api.domain.user;

import com.chaekingam.api.domain.review.ReviewService;
import com.chaekingam.api.domain.review.dto.ReviewResponse;
import com.chaekingam.api.domain.user.dto.*;
import com.chaekingam.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "유저 프로필", description = "내 프로필 조회·수정, 다른 사람 프로필 조회, 특정 유저 독후감 목록")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ReviewService reviewService;

    @Operation(summary = "내 프로필 조회", description = "로그인한 사용자의 프로필을 반환합니다. JWT 필요.")
    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> getMyProfile() {
        return ApiResponse.ok(userService.getMyProfile());
    }

    @Operation(summary = "내 프로필 수정", description = "닉네임, 소개, 프로필 이미지를 수정합니다. JWT 필요.")
    @PatchMapping("/me")
    public ApiResponse<UserProfileResponse> updateMyProfile(@RequestBody UpdateProfileRequest request) {
        return ApiResponse.ok(userService.updateMyProfile(request));
    }

    @Operation(summary = "독자 추천", description = "같은 책을 읽은 독자를 추천합니다. JWT 필요.")
    @GetMapping("/me/recommendations")
    public ApiResponse<List<UserRecommendationResponse>> getRecommendations() {
        return ApiResponse.ok(userService.getRecommendations());
    }

    @Operation(summary = "독서 통계", description = "월별 독서량 및 선호 장르를 반환합니다. JWT 필요.")
    @GetMapping("/me/stats")
    public ApiResponse<ReadingStatsResponse> getReadingStats() {
        return ApiResponse.ok(userService.getReadingStats());
    }

    @Operation(summary = "다른 유저 프로필 조회", description = "userId로 다른 사용자의 프로필을 조회합니다. 인증 불필요.")
    @GetMapping("/{userId}")
    public ApiResponse<UserProfileResponse> getUserProfile(@PathVariable Long userId) {
        return ApiResponse.ok(userService.getUserProfile(userId));
    }

    @Operation(summary = "특정 유저 독후감 목록", description = "userId에 해당하는 사용자의 독후감 목록을 반환합니다. 인증 불필요.")
    @GetMapping("/{userId}/reviews")
    public ApiResponse<List<ReviewResponse>> getUserReviews(@PathVariable Long userId) {
        return ApiResponse.ok(reviewService.getByUser(userId));
    }

    @Operation(summary = "사용자 검색", description = "닉네임으로 사용자를 검색합니다. 인증 불필요.")
    @GetMapping("/search")
    public ApiResponse<List<UserSummary>> searchUsers(@RequestParam String q) {
        return ApiResponse.ok(userService.searchUsers(q));
    }
}
