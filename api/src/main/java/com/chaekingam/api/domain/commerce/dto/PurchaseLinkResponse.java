package com.chaekingam.api.domain.commerce.dto;

import com.chaekingam.api.domain.commerce.PurchaseLink;
import com.chaekingam.api.domain.commerce.PurchaseProvider;

public record PurchaseLinkResponse(
        Long id,
        PurchaseProvider provider,
        String url
) {
    public static PurchaseLinkResponse from(PurchaseLink link) {
        return new PurchaseLinkResponse(link.getId(), link.getProvider(), link.getUrl());
    }
}
