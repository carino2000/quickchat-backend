package org.example.quickchat.dto.response.chat;

import lombok.Getter;
import lombok.Setter;
import org.example.quickchat.domain.entity.Chat;

@Getter
@Setter
public class ChatResponse {
    Chat chat;
}
