package org.example.quickchat.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class ChatSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne
    @JsonIgnore
    Chat chat;
    @ManyToOne
    Member member;
    LocalDateTime joinAt;
    LocalDateTime lastActiveAt;

    @PrePersist
    public void prePersist() {
        this.joinAt = LocalDateTime.now();
        this.lastActiveAt = LocalDateTime.now();
    }
}
