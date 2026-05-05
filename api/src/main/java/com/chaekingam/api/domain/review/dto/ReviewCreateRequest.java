package com.chaekingam.api.domain.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReviewCreateRequest(
        Long bookId,
        @NotBlank String title,
        @NotBlank String content,
        @Min(1) @Max(5) int rating
) {
}
