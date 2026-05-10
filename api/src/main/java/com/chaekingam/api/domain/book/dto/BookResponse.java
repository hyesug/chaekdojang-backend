package com.chaekingam.api.domain.book.dto;

import com.chaekingam.api.domain.book.Book;

public record BookResponse(
        Long id,
        String isbn13,
        String title,
        String author,
        String publisher,
        String thumbnail,
        String source,
        String category
) {
    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(),
                book.getIsbn13(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getThumbnail(),
                book.getSource().name(),
                book.getCategory()
        );
    }
}
