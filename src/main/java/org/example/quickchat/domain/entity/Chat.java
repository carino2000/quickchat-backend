package org.example.quickchat.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Chat {
    @Id
    String id;
    @ManyToOne
    Member owner;
    String title;
    int currentUserCount;
    LocalDateTime openAt;
    String signature;

    @OneToMany(mappedBy = "chat") // many에 해당하는 entity의 컬럼명 작성
    List<ChatSession> chatSessions;

    @PrePersist
    public void prePersist() {
        this.id = UUID.randomUUID().toString().split("-")[4];
        this.openAt = LocalDateTime.now();
    }
}
