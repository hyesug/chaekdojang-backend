package com.chaekingam.api.domain.commerce.dto;

import com.chaekingam.api.domain.commerce.PurchaseProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PurchaseLinkAddRequest(
        @NotNull Long bookId,
        @NotNull PurchaseProvider provider,
        @NotBlank String url
) {
}
