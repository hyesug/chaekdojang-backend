package com.chaekdojang.api.domain.subscription.dto;

import com.chaekdojang.api.domain.subscription.SubscriptionPlan;

public record SubscribeRequest(SubscriptionPlan plan, String paymentKey, String orderId, int amount) {}
