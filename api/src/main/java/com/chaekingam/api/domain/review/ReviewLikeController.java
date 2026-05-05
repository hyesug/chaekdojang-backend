package com.chaekingam.api.domain.review;

import com.chaekingam.api.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews/{reviewId}/like")
@RequiredArgsConstructor
public class ReviewLikeController {

    private final ReviewLikeService reviewLikeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> like(@PathVariable Long reviewId) {
        reviewLikeService.like(reviewId);
        return ApiResponse.ok();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlike(@PathVariable Long reviewId) {
        reviewLikeService.unlike(reviewId);
    }

    @GetMapping("/count")
    public ApiResponse<Long> count(@PathVariable Long reviewId) {
        return ApiResponse.ok(reviewLikeService.countLikes(reviewId));
    }
}
