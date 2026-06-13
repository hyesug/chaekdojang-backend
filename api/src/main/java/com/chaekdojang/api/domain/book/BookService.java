package com.chaekdojang.api.domain.book;

import com.chaekdojang.api.domain.book.dto.BookResponse;
import com.chaekdojang.api.domain.book.dto.BookSearchResult;
import com.chaekdojang.api.global.exception.CustomException;
import com.chaekdojang.api.global.exception.ErrorCode;
import com.chaekdojang.api.infra.google.GoogleBookClient;
import com.chaekdojang.api.infra.kakao.KakaoBookClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final KakaoBookClient kakaoBookClient;
    private final GoogleBookClient googleBookClient;

    @Transactional
    public List<BookResponse> search(String query) {
        List<BookSearchResult> results = new ArrayList<>();
        results.addAll(kakaoBookClient.search(query));
        results.addAll(googleBookClient.search(query));

        // isbn13 기준 중복 제거 (카카오 우선)
        Map<String, BookSearchResult> deduped = new LinkedHashMap<>();
        for (BookSearchResult r : results) {
            deduped.putIfAbsent(r.isbn13(), r);
        }

        return deduped.values().stream()
                .map(this::upsertBook)
                .map(BookResponse::from)
                .toList();
    }

    public BookResponse findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOK_NOT_FOUND));
        return BookResponse.from(book);
    }

    public List<BookResponse> findByCategory(String category) {
        return bookRepository.findAllByCategoryContainingIgnoreCase(category)
                .stream()
                .map(BookResponse::from)
                .toList();
    }

    private Book upsertBook(BookSearchResult result) {
        return bookRepository.findByIsbn13(result.isbn13())
                .orElseGet(() -> bookRepository.save(
                        Book.builder()
                                .isbn13(result.isbn13())
                                .title(result.title())
                                .author(result.author())
                                .publisher(result.publisher())
                                .thumbnail(result.thumbnail())
                                .source(result.source())
                                .category(result.category())
                                .build()
                ));
    }
}
