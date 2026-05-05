package com.chaekingam.api.domain.review;

import com.chaekingam.api.domain.book.Book;
import com.chaekingam.api.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private int rating;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime deletedAt;

    @Builder
    private Review(Book book, User author, String title, String content, int rating) {
        this.book = book;
        this.author = author;
        this.title = title;
        this.content = content;
        this.rating = rating;
    }

    public void update(String title, String content, int rating) {
        this.title = title;
        this.content = content;
        this.rating = rating;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isAuthor(Long userId) {
        return this.author.getId().equals(userId);
    }
}
