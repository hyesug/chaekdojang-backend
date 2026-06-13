package com.chaekdojang.api.domain.subscription;

import com.chaekdojang.api.domain.subscription.dto.SubscribeRequest;
import com.chaekdojang.api.domain.subscription.dto.SubscriptionResponse;
import com.chaekdojang.api.domain.user.User;
import com.chaekdojang.api.domain.user.UserRepository;
import com.chaekdojang.api.global.exception.CustomException;
import com.chaekdojang.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final WebClient webClient;

    @Value("${toss.secret-key:}")
    private String tossSecretKey;

    public SubscriptionResponse subscribe(Long userId, SubscribeRequest req) {
        if (subscriptionRepository.existsByUserIdAndActiveTrue(userId)) {
            throw new CustomException(ErrorCode.SUBSCRIPTION_ALREADY_EXISTS);
        }

        // 토스페이먼츠 결제 승인
        if (!tossSecretKey.isBlank()) {
            confirmTossPayment(req.paymentKey(), req.orderId(), req.amount());
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Subscription subscription = subscriptionRepository.save(
                Subscription.builder()
                        .user(user)
                        .plan(req.plan())
                        .paymentKey(req.paymentKey())
                        .build()
        );
        return SubscriptionResponse.from(subscription);
    }

    @Transactional(readOnly = true)
    public Optional<SubscriptionResponse> getMySubscription(Long userId) {
        return subscriptionRepository.findByUserIdAndActiveTrue(userId)
                .map(SubscriptionResponse::from);
    }

    @Transactional(readOnly = true)
    public boolean isPremium(Long userId) {
        return subscriptionRepository.findByUserIdAndActiveTrue(userId)
                .map(s -> !s.isExpired())
                .orElse(false);
    }

    public void cancel(Long userId) {
        Subscription subscription = subscriptionRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SUBSCRIPTION_NOT_FOUND));
        subscription.cancel();
    }

    private void confirmTossPayment(String paymentKey, String orderId, int amount) {
        String encoded = Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes());
        try {
            webClient.post()
                    .uri("https://api.tosspayments.com/v1/payments/confirm")
                    .header("Authorization", "Basic " + encoded)
                    .header("Content-Type", "application/json")
                    .bodyValue(Map.of(
                            "paymentKey", paymentKey,
                            "orderId", orderId,
                            "amount", amount
                    ))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.TRANSLATION_FAILED);
        }
    }
}
