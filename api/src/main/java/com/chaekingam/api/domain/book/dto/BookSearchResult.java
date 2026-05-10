package com.chaekingam.api.domain.book.dto;

import com.chaekingam.api.domain.book.BookSource;

public record BookSearchResult(
        String isbn13,
        String title,
        String author,
        String publisher,
        String thumbnail,
        BookSource source,
        String category
) {
}
