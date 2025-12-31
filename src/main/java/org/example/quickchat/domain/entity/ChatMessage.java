package org.example.quickchat.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne
    @JsonIgnore
    Chat chat;
    @ManyToOne
    Member talker;
    String content;
    LocalDateTime talkedAt;

    @PrePersist
    public void prePersist() {
        this.talkedAt = LocalDateTime.now();
    }
}
