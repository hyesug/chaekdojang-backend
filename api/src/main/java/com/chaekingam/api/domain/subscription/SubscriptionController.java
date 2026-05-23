package com.chaekingam.api.domain.subscription;

import com.chaekingam.api.domain.subscription.dto.SubscribeRequest;
import com.chaekingam.api.domain.subscription.dto.SubscriptionResponse;
import com.chaekingam.api.global.response.ApiResponse;
import com.chaekingam.api.global.security.OAuthUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/me")
    public ApiResponse<?> getMySubscription(@AuthenticationPrincipal OAuthUserPrincipal principal) {
        return subscriptionService.getMySubscription(principal.getUserId())
                .<ApiResponse<?>>map(ApiResponse::ok)
                .orElse(ApiResponse.ok(null));
    }

    @GetMapping("/me/status")
    public ApiResponse<Map<String, Boolean>> isPremium(@AuthenticationPrincipal OAuthUserPrincipal principal) {
        boolean premium = subscriptionService.isPremium(principal.getUserId());
        return ApiResponse.ok(Map.of("premium", premium));
    }

    @PostMapping
    public ApiResponse<SubscriptionResponse> subscribe(
            @AuthenticationPrincipal OAuthUserPrincipal principal,
            @RequestBody SubscribeRequest request) {
        return ApiResponse.ok(subscriptionService.subscribe(principal.getUserId(), request));
    }

    @DeleteMapping("/me")
    public ApiResponse<Void> cancel(@AuthenticationPrincipal OAuthUserPrincipal principal) {
        subscriptionService.cancel(principal.getUserId());
        return ApiResponse.ok(null);
    }
}
