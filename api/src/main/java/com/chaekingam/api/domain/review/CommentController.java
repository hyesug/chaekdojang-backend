package com.chaekingam.api.domain.review;

import com.chaekingam.api.domain.review.dto.CommentCreateRequest;
import com.chaekingam.api.domain.review.dto.CommentResponse;
import com.chaekingam.api.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews/{reviewId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CommentResponse> create(
            @PathVariable Long reviewId,
            @RequestBody @Valid CommentCreateRequest request) {
        return ApiResponse.ok(commentService.create(reviewId, request));
    }

    @GetMapping
    public ApiResponse<List<CommentResponse>> getAll(@PathVariable Long reviewId) {
        return ApiResponse.ok(commentService.getAll(reviewId));
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long reviewId, @PathVariable Long commentId) {
        commentService.delete(reviewId, commentId);
    }
}
