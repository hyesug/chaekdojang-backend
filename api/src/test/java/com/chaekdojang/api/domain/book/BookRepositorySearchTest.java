package com.chaekdojang.api.domain.book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BookRepositorySearchTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    void findsStoredBookWhenTitleSearchOmitsPunctuation() {
        bookRepository.saveAndFlush(Book.builder()
                .isbn13("9791139233803")
                .title("그리고 다시, 우리")
                .author("진성림")
                .publisher("지식과감성")
                .source(BookSource.KAKAO)
                .build());

        assertThat(bookRepository.searchByFilters("그리고다시우리", "", ""))
                .extracting(Book::getIsbn13)
                .containsExactly("9791139233803");
    }
}
