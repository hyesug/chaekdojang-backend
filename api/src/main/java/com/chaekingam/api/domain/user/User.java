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

    // 이메일이 없는 OAuth 제공자(카카오 비동의 등)도 가입 가능하도록 nullable
    @Column(unique = true)
    private String email;

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

    public static User create(String email, String nickname, String profileImage) {
        User user = new User();
        user.email = email;
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

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
