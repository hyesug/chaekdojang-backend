package com.chaekingam.api.domain.book;

import com.chaekingam.api.domain.book.dto.BookResponse;
import com.chaekingam.api.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/search")
    public ApiResponse<List<BookResponse>> search(@RequestParam String q) {
        return ApiResponse.ok(bookService.search(q));
    }
}
