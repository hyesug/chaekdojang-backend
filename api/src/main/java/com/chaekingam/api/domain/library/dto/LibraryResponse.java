package com.chaekingam.api.domain.library.dto;

import com.chaekingam.api.domain.book.Book;
import com.chaekingam.api.domain.library.Library;
import com.chaekingam.api.domain.library.LibraryStatus;

import java.time.LocalDateTime;

public record LibraryResponse(
        Long id,
        BookInfo book,
        LibraryStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public record BookInfo(Long id, String isbn13, String title, String author, String thumbnail) {
        public static BookInfo from(Book book) {
            return new BookInfo(book.getId(), book.getIsbn13(), book.getTitle(),
                    book.getAuthor(), book.getThumbnail());
        }
    }

    public static LibraryResponse from(Library library) {
        return new LibraryResponse(
                library.getId(),
                BookInfo.from(library.getBook()),
                library.getStatus(),
                library.getCreatedAt(),
                library.getUpdatedAt()
        );
    }
}
