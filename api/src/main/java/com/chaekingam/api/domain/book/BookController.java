package com.chaekingam.api.domain.book;

import com.chaekingam.api.domain.book.dto.BookResponse;
import com.chaekingam.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "도서", description = "도서 검색 (카카오 + Google Books)")
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @Operation(summary = "도서 검색", description = "카카오 책 API와 Google Books API를 통합 검색합니다. 인증 불필요.")
    @GetMapping("/search")
    public ApiResponse<List<BookResponse>> search(
            @Parameter(description = "검색 키워드 (예: 채식주의자)", required = true)
            @RequestParam String q) {
        return ApiResponse.ok(bookService.search(q));
    }
}
