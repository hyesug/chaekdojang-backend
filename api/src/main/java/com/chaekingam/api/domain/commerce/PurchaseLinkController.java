package com.chaekingam.api.domain.commerce;

import com.chaekingam.api.domain.commerce.dto.PurchaseLinkAddRequest;
import com.chaekingam.api.domain.commerce.dto.PurchaseLinkResponse;
import com.chaekingam.api.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class PurchaseLinkController {

    private final PurchaseLinkService purchaseLinkService;

    @GetMapping("/{bookId}/purchase-links")
    public ApiResponse<List<PurchaseLinkResponse>> getLinks(@PathVariable Long bookId) {
        return ApiResponse.ok(purchaseLinkService.getLinks(bookId));
    }

    @PostMapping("/purchase-links")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PurchaseLinkResponse> addLink(
            @RequestBody @Valid PurchaseLinkAddRequest request) {
        return ApiResponse.ok(purchaseLinkService.addLink(request));
    }
}
