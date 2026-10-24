package com.chaekdojang.api.domain.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.chaekdojang.api.domain.book.dto.BookResponse;
import com.chaekdojang.api.domain.book.dto.BookSearchResult;
import com.chaekdojang.api.domain.review.CommentRepository;
import com.chaekdojang.api.domain.review.ReviewLikeRepository;
import com.chaekdojang.api.domain.review.ReviewRepository;
import com.chaekdojang.api.domain.review.ai.ReviewAiSummaryRepository;
import com.chaekdojang.api.infra.google.GoogleBookClient;
import com.chaekdojang.api.infra.kakao.KakaoBookClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookServiceTest {

    private BookRepository bookRepository;
    private KakaoBookClient kakaoBookClient;
    private GoogleBookClient googleBookClient;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookRepository = mock(BookRepository.class);
        kakaoBookClient = mock(KakaoBookClient.class);
        googleBookClient = mock(GoogleBookClient.class);
        BookCategoryResolver bookCategoryResolver = mock(BookCategoryResolver.class);
        StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);

        when(bookRepository.searchByFilters(anyString(), anyString(), anyString())).thenReturn(List.of());
        when(bookRepository.findByIsbn13(anyString())).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(googleBookClient.search(anyString())).thenReturn(List.of());
        when(bookCategoryResolver.resolveAll(any())).thenReturn(Map.of());
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        bookService = new BookService(
                bookRepository,
                kakaoBookClient,
                googleBookClient,
                mock(WebNovelService.class),
                bookCategoryResolver,
                mock(BookCategoryVerificationService.class),
                mock(ReviewRepository.class),
                mock(ReviewAiSummaryRepository.class),
                mock(ReviewLikeRepository.class),
                mock(CommentRepository.class),
                redisTemplate,
                new ObjectMapper()
        );
    }

    @Test
    void findsBookWhenTitleSearchOmitsPunctuation() {
        BookSearchResult target = targetBook();
        when(kakaoBookClient.search("그리고 다시 우리")).thenReturn(List.of(target));

        List<BookResponse> results = bookService.search("그리고 다시 우리", "", "");

        assertThat(results).extracting(BookResponse::isbn13).containsExactly("9791139233803");
    }

    @Test
    void findsBookWhenFreeTextSearchMatchesAuthor() {
        BookSearchResult target = targetBook();
        when(kakaoBookClient.search("진성림")).thenReturn(List.of(target));

        List<BookResponse> results = bookService.search("진성림", "", "");

        assertThat(results).extracting(BookResponse::isbn13).containsExactly("9791139233803");
    }

    @Test
    void returnsNoResultsWhenSearchInputContainsOnlyPunctuation() {
        when(kakaoBookClient.search(",")).thenReturn(List.of(targetBook()));

        List<BookResponse> results = bookService.search(",", "", "");

        assertThat(results).isEmpty();
    }

    private BookSearchResult targetBook() {
        return new BookSearchResult(
                "9791139233803",
                "그리고 다시, 우리",
                "진성림",
                "지식과감성",
                "https://example.com/cover.jpg",
                BookSource.KAKAO,
                "소설",
                "장편소설"
        );
    }
}
