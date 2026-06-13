package com.chaekdojang.api.domain.review;

import com.chaekdojang.api.domain.review.dto.CommentCreateRequest;
import com.chaekdojang.api.domain.review.dto.CommentResponse;
import com.chaekdojang.api.domain.review.dto.CommentUpdateRequest;
import com.chaekdojang.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "댓글", description = "독후감 댓글 작성·조회·삭제")
@RestController
@RequestMapping("/api/reviews/{reviewId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 작성", description = "특정 독후감에 댓글을 작성합니다. JWT 필요.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CommentResponse> create(
            @PathVariable Long reviewId,
            @RequestBody @Valid CommentCreateRequest request) {
        return ApiResponse.ok(commentService.create(reviewId, request));
    }

    @Operation(summary = "댓글 목록 조회", description = "특정 독후감의 댓글 목록을 반환합니다.")
    @GetMapping
    public ApiResponse<List<CommentResponse>> getAll(@PathVariable Long reviewId) {
        return ApiResponse.ok(commentService.getAll(reviewId));
    }

    @Operation(summary = "댓글 수정", description = "본인이 작성한 댓글을 수정합니다. JWT 필요.")
    @PutMapping("/{commentId}")
    public ApiResponse<CommentResponse> update(
            @PathVariable Long reviewId,
            @PathVariable Long commentId,
            @RequestBody @Valid CommentUpdateRequest request) {
        return ApiResponse.ok(commentService.update(reviewId, commentId, request));
    }

    @Operation(summary = "댓글 삭제", description = "본인이 작성한 댓글을 삭제합니다. JWT 필요.")
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long reviewId, @PathVariable Long commentId) {
        commentService.delete(reviewId, commentId);
    }
}
