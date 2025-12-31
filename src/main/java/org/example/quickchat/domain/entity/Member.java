package org.example.quickchat.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String provider;
    String providerId;
    String name;
    String email;
    String picture;
    LocalDateTime joinAt;

    public Member() {
    }

    public Member(String provider, String providerId, String name, String email, String picture) {
        this.provider = provider;
        this.providerId = providerId;
        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    @PrePersist
    public void prePersist() {
        this.joinAt = LocalDateTime.now();
    }
}
