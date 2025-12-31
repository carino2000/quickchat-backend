package org.example.quickchat.dto.response.chat;

import lombok.Getter;
import lombok.Setter;
import org.example.quickchat.domain.entity.Chat;
import org.example.quickchat.domain.entity.ChatMessage;

import java.util.List;

@Getter
@Setter
public class GetChatMessageResponse {
    List<ChatMessage> chatMessages;

    public GetChatMessageResponse() {}

    public GetChatMessageResponse(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }
}
