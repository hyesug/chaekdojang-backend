package com.chaekingam.api.domain.subscription.dto;

import com.chaekingam.api.domain.subscription.SubscriptionPlan;

public record SubscribeRequest(SubscriptionPlan plan, String paymentKey, String orderId, int amount) {}
