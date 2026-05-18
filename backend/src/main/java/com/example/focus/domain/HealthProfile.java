package com.example.focus.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_health_profiles") // 명세서 매핑 구조 동기화
@Getter
@NoArgsConstructor
public class HealthProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 🔥 [교정] 엔티티 클래스 대신 명세서 및 서비스 코드와 일치하도록 값 타입 컬렉션(List<String>) 매핑
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_conditions", joinColumns = @JoinColumn(name = "health_profile_id"))
    @Column(name = "condition_code", nullable = false)
    private List<String> conditions = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_allergies", joinColumns = @JoinColumn(name = "health_profile_id"))
    @Column(name = "allergy_code", nullable = false)
    private List<String> allergies = new ArrayList<>();

    public HealthProfile(User user) {
        this.user = user;
        this.updatedAt = LocalDateTime.now();
    }

    // 연관관계 편의 메서드를 받아주기 위한 setter 구조
    public void setUser(User user) {
        this.user = user;
    }

    @PreUpdate
    @PrePersist
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
}