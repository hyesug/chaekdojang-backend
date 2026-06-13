package com.chaekdojang.api.domain.subscription;

import com.chaekdojang.api.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionPlan plan;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean active;

    // 토스페이먼츠 결제 키 저장
    private String paymentKey;

    @Builder
    private Subscription(User user, SubscriptionPlan plan, String paymentKey) {
        this.user = user;
        this.plan = plan;
        this.startedAt = LocalDateTime.now();
        this.expiresAt = plan == SubscriptionPlan.YEARLY
                ? LocalDateTime.now().plusYears(1)
                : LocalDateTime.now().plusMonths(1);
        this.active = true;
        this.paymentKey = paymentKey;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public void cancel() {
        this.active = false;
    }
}
