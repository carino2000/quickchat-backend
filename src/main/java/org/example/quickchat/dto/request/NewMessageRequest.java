package org.example.quickchat.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.example.quickchat.domain.entity.Chat;
import org.example.quickchat.domain.entity.ChatMessage;
import org.example.quickchat.domain.entity.Member;

@Getter
@Setter
@Valid
public class NewMessageRequest {
    @NotBlank
    String content;

    public ChatMessage toChatMessage(Member talker, Chat chat) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChat(chat);
        chatMessage.setTalker(talker);
        chatMessage.setContent(this.content);
        return chatMessage;
    }
}
