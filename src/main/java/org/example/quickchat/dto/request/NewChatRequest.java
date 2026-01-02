package org.example.quickchat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Schema(description = "채팅방 생성 및 조회 요청객체")
@Getter
@Setter
public class NewChatRequest {
    @Schema(description = "상대방 아이디" ,example = "41861")
    Long targetId;
}
