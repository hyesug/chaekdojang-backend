package com.chaekingam.api.domain.commerce;

import com.chaekingam.api.domain.commerce.dto.PurchaseLinkAddRequest;
import com.chaekingam.api.domain.commerce.dto.PurchaseLinkResponse;
import com.chaekingam.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "구매 링크", description = "도서 구매 링크 조회·등록 (쿠팡파트너스·교보)")
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class PurchaseLinkController {

    private final PurchaseLinkService purchaseLinkService;

    @Operation(summary = "도서 구매 링크 조회", description = "특정 도서의 쿠팡파트너스·교보 구매 링크 목록을 반환합니다.")
    @GetMapping("/{bookId}/purchase-links")
    public ApiResponse<List<PurchaseLinkResponse>> getLinks(@PathVariable Long bookId) {
        return ApiResponse.ok(purchaseLinkService.getLinks(bookId));
    }

    @Operation(summary = "구매 링크 등록", description = "도서에 구매 링크를 추가합니다. JWT 필요.")
    @PostMapping("/purchase-links")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PurchaseLinkResponse> addLink(
            @RequestBody @Valid PurchaseLinkAddRequest request) {
        return ApiResponse.ok(purchaseLinkService.addLink(request));
    }
}
