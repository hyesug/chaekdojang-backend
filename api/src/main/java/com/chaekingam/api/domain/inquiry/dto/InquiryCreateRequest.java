package com.chaekingam.api.domain.inquiry.dto;

import jakarta.validation.constraints.NotBlank;

public record InquiryCreateRequest(
        @NotBlank String title,
        @NotBlank String content,
        String guestName,
        String guestEmail
) {}
