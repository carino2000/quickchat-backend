package org.example.quickchat.dto.projection;

import lombok.Getter;
import lombok.Setter;
import org.example.quickchat.domain.entity.Chat;
import org.example.quickchat.domain.entity.ChatMessage;

@Getter
@Setter
public class ChatAndLastMsg {
    Chat chat;
    ChatMessage lastMsg;
    int unReadCount;

    public ChatAndLastMsg() {}

    public ChatAndLastMsg(Chat chat, ChatMessage lastMsg, int unReadCount) {
        this.chat = chat;
        this.lastMsg = lastMsg;
        this.unReadCount = unReadCount;
    }
}
