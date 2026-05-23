package com.chaekingam.api.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AuthProvider provider;

    @Column(nullable = false)
    private String providerId;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column
    private String profileImage;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(nullable = false, length = 10)
    private String language;

    @Column(nullable = false, length = 10)
    private String country;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime deletedAt;

    public static User createOAuth(String email, String providerId, String nickname,
                                    String profileImage, AuthProvider provider) {
        User user = new User();
        user.email = email;
        user.providerId = providerId;
        user.provider = provider;
        user.nickname = nickname;
        user.profileImage = profileImage;
        user.language = "ko";
        user.country = "KR";
        return user;
    }

    public void updateProfile(String nickname, String bio, String profileImage) {
        if (nickname != null) this.nickname = nickname;
        if (bio != null) this.bio = bio;
        if (profileImage != null) this.profileImage = profileImage;
    }
}
