package com.chaekingam.api.domain.commerce;

import com.chaekingam.api.domain.book.Book;
import com.chaekingam.api.domain.book.BookRepository;
import com.chaekingam.api.domain.commerce.dto.PurchaseLinkAddRequest;
import com.chaekingam.api.domain.commerce.dto.PurchaseLinkResponse;
import com.chaekingam.api.global.exception.CustomException;
import com.chaekingam.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseLinkService {

    private final PurchaseLinkRepository purchaseLinkRepository;
    private final BookRepository bookRepository;

    public List<PurchaseLinkResponse> getLinks(Long bookId) {
        return purchaseLinkRepository.findAllByBookId(bookId)
                .stream()
                .map(PurchaseLinkResponse::from)
                .toList();
    }

    @Transactional
    public PurchaseLinkResponse addLink(PurchaseLinkAddRequest request) {
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new CustomException(ErrorCode.BOOK_NOT_FOUND));
        PurchaseLink link = PurchaseLink.builder()
                .book(book)
                .provider(request.provider())
                .url(request.url())
                .build();
        return PurchaseLinkResponse.from(purchaseLinkRepository.save(link));
    }
}
