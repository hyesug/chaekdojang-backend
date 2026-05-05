package com.chaekingam.api.domain.review;

import com.chaekingam.api.domain.review.dto.ReviewCreateRequest;
import com.chaekingam.api.domain.review.dto.ReviewResponse;
import com.chaekingam.api.domain.review.dto.ReviewUpdateRequest;
import com.chaekingam.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "독후감", description = "독후감 작성·조회·수정·삭제")
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "독후감 작성", description = "로그인한 사용자가 새 독후감을 작성합니다. JWT 필요.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReviewResponse> create(@RequestBody @Valid ReviewCreateRequest request) {
        return ApiResponse.ok(reviewService.create(request));
    }

    @Operation(summary = "독후감 피드 전체 조회", description = "모든 사용자의 독후감 목록을 반환합니다. 인증 불필요.")
    @GetMapping
    public ApiResponse<List<ReviewResponse>> getAll() {
        return ApiResponse.ok(reviewService.getAll());
    }

    @Operation(summary = "독후감 단건 조회", description = "독후감 ID로 특정 독후감을 조회합니다. 인증 불필요.")
    @GetMapping("/{id}")
    public ApiResponse<ReviewResponse> getOne(@PathVariable Long id) {
        return ApiResponse.ok(reviewService.getOne(id));
    }

    @Operation(summary = "독후감 수정", description = "본인이 작성한 독후감의 내용을 수정합니다. JWT 필요.")
    @PutMapping("/{id}")
    public ApiResponse<ReviewResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid ReviewUpdateRequest request
    ) {
        return ApiResponse.ok(reviewService.update(id, request));
    }

    @Operation(summary = "독후감 삭제", description = "본인이 작성한 독후감을 삭제합니다. JWT 필요.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        reviewService.delete(id);
    }
}
