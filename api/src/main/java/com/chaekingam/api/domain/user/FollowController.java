package com.chaekingam.api.domain.user;

import com.chaekingam.api.domain.user.dto.UserSummary;
import com.chaekingam.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "팔로우", description = "팔로우·언팔로우·팔로워·팔로잉 목록")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @Operation(summary = "팔로우", description = "특정 사용자를 팔로우합니다. JWT 필요.")
    @PostMapping("/{userId}/follow")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> follow(@PathVariable Long userId) {
        followService.follow(userId);
        return ApiResponse.ok();
    }

    @Operation(summary = "언팔로우", description = "팔로우 중인 사용자를 언팔로우합니다. JWT 필요.")
    @DeleteMapping("/{userId}/follow")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfollow(@PathVariable Long userId) {
        followService.unfollow(userId);
    }

    @Operation(summary = "팔로워 목록 조회", description = "특정 사용자를 팔로우하는 사람 목록을 반환합니다. 인증 불필요.")
    @GetMapping("/{userId}/followers")
    public ApiResponse<List<UserSummary>> getFollowers(@PathVariable Long userId) {
        return ApiResponse.ok(followService.getFollowers(userId));
    }

    @Operation(summary = "팔로잉 목록 조회", description = "특정 사용자가 팔로우하는 사람 목록을 반환합니다. 인증 불필요.")
    @GetMapping("/{userId}/followings")
    public ApiResponse<List<UserSummary>> getFollowings(@PathVariable Long userId) {
        return ApiResponse.ok(followService.getFollowings(userId));
    }
}
