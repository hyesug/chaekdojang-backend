package com.chaekingam.api.domain.review;

import com.chaekingam.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "좋아요", description = "독후감 좋아요 추가·취소·수 조회")
@RestController
@RequestMapping("/api/reviews/{reviewId}/like")
@RequiredArgsConstructor
public class ReviewLikeController {

    private final ReviewLikeService reviewLikeService;

    @Operation(summary = "좋아요 추가", description = "특정 독후감에 좋아요를 누릅니다. JWT 필요.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> like(@PathVariable Long reviewId) {
        reviewLikeService.like(reviewId);
        return ApiResponse.ok();
    }

    @Operation(summary = "좋아요 취소", description = "이미 누른 좋아요를 취소합니다. JWT 필요.")
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlike(@PathVariable Long reviewId) {
        reviewLikeService.unlike(reviewId);
    }

    @Operation(summary = "좋아요 수 조회", description = "특정 독후감의 좋아요 총 개수를 반환합니다.")
    @GetMapping("/count")
    public ApiResponse<Long> count(@PathVariable Long reviewId) {
        return ApiResponse.ok(reviewLikeService.countLikes(reviewId));
    }

    @Operation(summary = "좋아요 여부 확인", description = "로그인한 사용자가 해당 독후감에 좋아요를 눌렀는지 반환합니다. JWT 필요.")
    @GetMapping("/status")
    public ApiResponse<Boolean> status(@PathVariable Long reviewId) {
        return ApiResponse.ok(reviewLikeService.isLiked(reviewId));
    }
}
