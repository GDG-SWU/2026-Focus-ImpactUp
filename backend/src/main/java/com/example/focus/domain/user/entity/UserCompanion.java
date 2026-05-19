package com.example.focus.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_companions")
@Getter
@NoArgsConstructor
public class UserCompanion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "companion_type", nullable = false, length = 50)
    private String companionType;

    public UserCompanion(User user, String companionType) {
        this.user = user;
        this.companionType = companionType;
    }
}