package com.chaekingam.api.domain.book;

import com.chaekingam.api.domain.book.dto.BookResponse;
import com.chaekingam.api.domain.review.ReviewService;
import com.chaekingam.api.domain.review.dto.ReviewResponse;
import com.chaekingam.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "도서", description = "도서 검색 (카카오 + Google Books)")
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final ReviewService reviewService;

    @Operation(summary = "도서 검색", description = "카카오 책 API와 Google Books API를 통합 검색합니다. 인증 불필요.")
    @GetMapping("/search")
    public ApiResponse<List<BookResponse>> search(
            @Parameter(description = "검색 키워드 (예: 채식주의자)", required = true)
            @RequestParam String q) {
        return ApiResponse.ok(bookService.search(q));
    }

    @Operation(summary = "카테고리별 도서 조회", description = "특정 카테고리에 속한 도서 목록을 반환합니다. 인증 불필요.")
    @GetMapping("/category")
    public ApiResponse<List<BookResponse>> getByCategory(
            @Parameter(description = "카테고리명 (예: 소설, 자기계발)", required = true)
            @RequestParam String name) {
        return ApiResponse.ok(bookService.findByCategory(name));
    }

    @Operation(summary = "도서 단건 조회", description = "도서 ID로 특정 책 정보를 반환합니다. 인증 불필요.")
    @GetMapping("/{id}")
    public ApiResponse<BookResponse> getOne(@PathVariable Long id) {
        return ApiResponse.ok(bookService.findById(id));
    }

    @Operation(summary = "책별 독후감 목록", description = "특정 책에 대해 작성된 독후감 목록을 반환합니다. 인증 불필요.")
    @GetMapping("/{id}/reviews")
    public ApiResponse<List<ReviewResponse>> getReviewsByBook(
            @Parameter(description = "책 ID", required = true)
            @PathVariable Long id) {
        return ApiResponse.ok(reviewService.getByBook(id));
    }
}
