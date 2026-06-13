package com.chaekdojang.api.domain.commerce.dto;

import com.chaekdojang.api.domain.commerce.PurchaseLink;
import com.chaekdojang.api.domain.commerce.PurchaseProvider;

public record PurchaseLinkResponse(
        Long id,
        PurchaseProvider provider,
        String url
) {
    public static PurchaseLinkResponse from(PurchaseLink link) {
        return new PurchaseLinkResponse(link.getId(), link.getProvider(), link.getUrl());
    }
}
