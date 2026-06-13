package com.chaekdojang.api.domain.review;

import com.chaekdojang.api.domain.review.dto.ReviewResponse;
import com.chaekdojang.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "독후감 북마크", description = "독후감 저장·해제·목록")
@RestController
@RequiredArgsConstructor
public class ReviewBookmarkController {

    private final ReviewBookmarkService bookmarkService;

    @Operation(summary = "북마크 추가", description = "JWT 필요.")
    @PostMapping("/api/reviews/{id}/bookmark")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> bookmark(@PathVariable Long id) {
        bookmarkService.bookmark(id);
        return ApiResponse.ok();
    }

    @Operation(summary = "북마크 해제", description = "JWT 필요.")
    @DeleteMapping("/api/reviews/{id}/bookmark")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unbookmark(@PathVariable Long id) {
        bookmarkService.unbookmark(id);
    }

    @Operation(summary = "북마크 여부 확인", description = "JWT 필요.")
    @GetMapping("/api/reviews/{id}/bookmark/status")
    public ApiResponse<Boolean> isBookmarked(@PathVariable Long id) {
        return ApiResponse.ok(bookmarkService.isBookmarked(id));
    }

    @Operation(summary = "내 북마크 목록", description = "JWT 필요.")
    @GetMapping("/api/reviews/bookmarked")
    public ApiResponse<List<ReviewResponse>> getMyBookmarks() {
        return ApiResponse.ok(bookmarkService.getMyBookmarks());
    }
}
