package com.chaekingam.api.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

/**
 * 소셜 로그인 수단을 별도 테이블로 관리한다.
 * User 1 : N UserAuthProvider — 한 계정에 카카오/구글/네이버를 모두 연결할 수 있다.
 */
@Entity
@Table(
    name = "user_auth_providers",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_provider_user_id",
        columnNames = {"provider", "provider_user_id"}
    )
)
@Getter
@NoArgsConstructor(access = PROTECTED)
public class UserAuthProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AuthProvider provider;

    @Column(name = "provider_user_id", nullable = false)
    private String providerUserId;

    // 소셜 계정 이메일 (User.email과 다를 수 있음)
    @Column(name = "provider_email")
    private String providerEmail;

    @Column(name = "provider_profile_image")
    private String providerProfileImage;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static UserAuthProvider of(User user, AuthProvider provider,
                                      String providerUserId, String providerEmail,
                                      String providerProfileImage) {
        UserAuthProvider auth = new UserAuthProvider();
        auth.user = user;
        auth.provider = provider;
        auth.providerUserId = providerUserId;
        auth.providerEmail = providerEmail;
        auth.providerProfileImage = providerProfileImage;
        return auth;
    }
}
