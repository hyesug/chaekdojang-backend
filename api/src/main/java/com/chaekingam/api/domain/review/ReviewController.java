package com.chaekingam.api.domain.review;

import com.chaekingam.api.domain.review.dto.ReviewCreateRequest;
import com.chaekingam.api.domain.review.dto.ReviewResponse;
import com.chaekingam.api.domain.review.dto.ReviewUpdateRequest;
import com.chaekingam.api.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReviewResponse> create(@RequestBody @Valid ReviewCreateRequest request) {
        return ApiResponse.ok(reviewService.create(request));
    }

    @GetMapping
    public ApiResponse<List<ReviewResponse>> getAll() {
        return ApiResponse.ok(reviewService.getAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<ReviewResponse> getOne(@PathVariable Long id) {
        return ApiResponse.ok(reviewService.getOne(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<ReviewResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid ReviewUpdateRequest request
    ) {
        return ApiResponse.ok(reviewService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        reviewService.delete(id);
    }
}
