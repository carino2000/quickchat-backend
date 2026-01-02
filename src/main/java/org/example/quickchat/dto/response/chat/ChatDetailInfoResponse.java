package org.example.quickchat.dto.response.chat;

import lombok.Getter;
import lombok.Setter;
import org.example.quickchat.dto.projection.ChatAndLastMsg;

import java.util.List;

@Getter
@Setter
public class ChatDetailInfoResponse {
    List<ChatAndLastMsg> chatAndLastMsg;

    public ChatDetailInfoResponse(List<ChatAndLastMsg> chatAndLastMsg) {
        this.chatAndLastMsg = chatAndLastMsg;
    }
}
