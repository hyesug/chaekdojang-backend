package com.chaekingam.api.domain.inquiry;

import com.chaekingam.api.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "inquiries")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // 비회원용
    @Column
    private String guestName;

    @Column
    private String guestEmail;

    // 회원용 (nullable)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "inquiry", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<InquiryComment> comments = new ArrayList<>();

    public static Inquiry create(String title, String content, User user, String guestName, String guestEmail) {
        Inquiry i = new Inquiry();
        i.title = title;
        i.content = content;
        i.user = user;
        i.guestName = guestName;
        i.guestEmail = guestEmail;
        return i;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    /** 비회원 접근 검증용 이메일 */
    public String getContactEmail() {
        return user != null ? user.getEmail() : guestEmail;
    }

    public String getAuthorName() {
        return user != null ? user.getNickname() : guestName;
    }
}
