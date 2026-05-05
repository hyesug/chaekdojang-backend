package com.chaekingam.api.domain.commerce;

import com.chaekingam.api.domain.book.Book;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "purchase_links")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class PurchaseLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PurchaseProvider provider;

    @Column(nullable = false)
    private String url;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private PurchaseLink(Book book, PurchaseProvider provider, String url) {
        this.book = book;
        this.provider = provider;
        this.url = url;
    }
}
