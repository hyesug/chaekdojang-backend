package com.chaekingam.api.domain.subscription;

public enum SubscriptionPlan {
    MONTHLY(9900, "월간 프리미엄"),
    YEARLY(99000, "연간 프리미엄");

    public final int price;
    public final String label;

    SubscriptionPlan(int price, String label) {
        this.price = price;
        this.label = label;
    }
}
