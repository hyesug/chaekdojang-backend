package com.chaekingam.api.domain.user;

import com.chaekingam.api.domain.user.dto.UserSummary;
import com.chaekingam.api.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{userId}/follow")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> follow(@PathVariable Long userId) {
        followService.follow(userId);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{userId}/follow")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfollow(@PathVariable Long userId) {
        followService.unfollow(userId);
    }

    @GetMapping("/{userId}/followers")
    public ApiResponse<List<UserSummary>> getFollowers(@PathVariable Long userId) {
        return ApiResponse.ok(followService.getFollowers(userId));
    }

    @GetMapping("/{userId}/followings")
    public ApiResponse<List<UserSummary>> getFollowings(@PathVariable Long userId) {
        return ApiResponse.ok(followService.getFollowings(userId));
    }
}
