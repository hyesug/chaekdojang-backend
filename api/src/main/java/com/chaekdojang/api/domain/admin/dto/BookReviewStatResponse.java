package com.chaekdojang.api.domain.admin.dto;

public record BookReviewStatResponse(
        Long bookId,
        String title,
        String author,
        long reviewCount
) {}
