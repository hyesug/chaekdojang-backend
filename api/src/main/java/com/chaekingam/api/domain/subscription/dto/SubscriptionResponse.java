package com.chaekingam.api.domain.subscription.dto;

import com.chaekingam.api.domain.subscription.Subscription;
import com.chaekingam.api.domain.subscription.SubscriptionPlan;

import java.time.LocalDateTime;

public record SubscriptionResponse(
        Long id,
        SubscriptionPlan plan,
        String planLabel,
        LocalDateTime startedAt,
        LocalDateTime expiresAt,
        boolean active,
        boolean expired
) {
    public static SubscriptionResponse from(Subscription s) {
        return new SubscriptionResponse(
                s.getId(), s.getPlan(), s.getPlan().label,
                s.getStartedAt(), s.getExpiresAt(),
                s.isActive(), s.isExpired()
        );
    }
}
