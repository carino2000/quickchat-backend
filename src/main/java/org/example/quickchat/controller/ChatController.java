package org.example.quickchat.controller;

import com.auth0.jwt.JWT;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.quickchat.domain.entity.Chat;
import org.example.quickchat.domain.entity.ChatMessage;
import org.example.quickchat.domain.entity.ChatSession;
import org.example.quickchat.domain.entity.Member;
import org.example.quickchat.dto.projection.ChatAndLastMsg;
import org.example.quickchat.dto.request.NewChatRequest;
import org.example.quickchat.dto.request.NewMessageRequest;
import org.example.quickchat.dto.response.chat.ChatResponse;
import org.example.quickchat.dto.response.chat.GetChatMessageResponse;
import org.example.quickchat.repository.ChatMessageRepository;
import org.example.quickchat.repository.ChatRepository;
import org.example.quickchat.repository.ChatSessionRepository;
import org.example.quickchat.repository.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chats")
public class ChatController {
    final ChatRepository chatRepository;
    final ChatSessionRepository chatSessionRepository;
    final ChatMessageRepository chatMessageRepository;
    final MemberRepository memberRepository;

    // 채팅방 개설 or 이미 존재하는 채팅방 id 넘겨주기
    @PostMapping
    public ResponseEntity<?> createChat(@RequestBody NewChatRequest ncr,
                                        @RequestAttribute Long tokenId) {

        Member source = memberRepository.findById(tokenId).orElseGet(null);
        Member target = memberRepository.findById(ncr.getTargetId()).orElseGet(null);

        if (source == null || target == null) {
            return ResponseEntity.badRequest().build();
        }

        List<String> sorted = Stream.of(tokenId, ncr.getTargetId())
                .sorted()
                .map(m -> String.valueOf(m))
                .toList();

        String signature = String.join(":", sorted);

        Optional<Chat> found = chatRepository.findAll().stream()
                .filter(f -> f.getSignature().equals(signature))
                .findFirst();

        Chat responseChat = found.orElseGet(() -> {
            Chat chat = new Chat();
            chat.setOwner(source);
            chat.setCurrentUserCount(2);
            chat.setSignature(signature);
            chatRepository.save(chat);

            List<ChatSession> chatSessionList = Stream.of(source, target).map(m -> {
                ChatSession cs = new ChatSession();
                cs.setChat(chat);
                cs.setMember(m);
                return cs;
            }).toList();

            chatSessionRepository.saveAll(chatSessionList);
            return chat;
        });
        ChatResponse resp = new ChatResponse();
        resp.setChat(responseChat);

        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    // 참여중인 채팅방 전체 가져오기
    @GetMapping
    public ResponseEntity<?> getMyChats(@RequestAttribute Long tokenId) {
        List<ChatSession> list = chatSessionRepository.findAll();
        Member source = memberRepository.findById(tokenId).orElseGet(null);
        if (list.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else if (source == null) {
            return ResponseEntity.badRequest().build();
        }

        List<ChatAndLastMsg> myChat = list.stream()
                .filter(f -> f.getMember().equals(source))
                .map(m -> m.getChat())
                .distinct()
                .map(dm -> {
                    List<ChatMessage> allMsg = chatMessageRepository.findAll().stream()
                            .filter(f -> f.getChat().equals(dm))
                            .sorted((o1, o2) -> {
                                return o2.getTalkedAt().compareTo(o1.getTalkedAt());
                            }).toList();
                    ChatMessage lastChat = allMsg.isEmpty() ? null : allMsg.getFirst();

                    ChatSession cs = dm.getChatSessions().stream().filter(f-> f.getMember().equals(source)).findFirst().get();
                    int unReadCount = (int)allMsg.stream().filter(f->f.getTalkedAt().isAfter(cs.getLastActiveAt())).count();

                    ChatAndLastMsg cal = new ChatAndLastMsg(dm, lastChat, unReadCount);
                    return cal;
                }).toList();

        return ResponseEntity.ok().body(myChat);
    }

    // 해당 채팅방에 모든 메시지 가져오기
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<?> getChat(@PathVariable String chatId,
                                     @RequestAttribute Long tokenId) {
        Member source = memberRepository.findById(tokenId).orElseGet(()->null);
        List<ChatMessage> list = chatMessageRepository.findAll();
        Chat chat = chatRepository.findById(chatId).orElseGet(() -> null);

        if(source == null || list.isEmpty() || chat == null) {
            return ResponseEntity.notFound().build();
        }

        ChatSession chatSession = chat.getChatSessions().stream()
                .filter(f->f.getMember().equals(source))
                .findFirst().orElseGet(()->null);

        if(chatSession == null) {
            return ResponseEntity.badRequest().build();
        }

        List<ChatMessage> messageList = list.stream()
                .filter(f->f.getChat().equals(chat) && f.getTalkedAt().isAfter(chatSession.getJoinAt()))
                .sorted((o1,o2)->{
                    return o1.getTalkedAt().compareTo(o2.getTalkedAt());
                }).toList();

        chatSession.setLastActiveAt(LocalDateTime.now());
        chatSessionRepository.save(chatSession);

        GetChatMessageResponse resp = new GetChatMessageResponse(messageList);

        return ResponseEntity.ok().body(resp);
    }

    // 메시지 전송(1건 등록)
    @PostMapping("/{chatId}/message")
    public ResponseEntity<?> createChat(@PathVariable String chatId,
                                        @RequestAttribute Long tokenId,
                                        @RequestBody @Valid NewMessageRequest nmr,
                                        BindingResult bindingResult) {
        Member source = memberRepository.findById(tokenId).orElseGet(()->null);
        Chat chat = chatRepository.findById(chatId).orElseGet(()->null);
        ChatSession chatSession = chatSessionRepository.findAll().stream()
                .filter(f->f.getChat().equals(chat) && f.getMember().equals(source))
                .findFirst().orElseGet(() -> null);
        if (source == null || chat == null || chatSession == null || !chat.getChatSessions().contains(chatSession)) {
            return ResponseEntity.badRequest().build();
        }else if(bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        ChatMessage chatMessage = nmr.toChatMessage(source, chat);
        chatMessageRepository.save(chatMessage);

        chatSession.setLastActiveAt(LocalDateTime.now());
        chatSessionRepository.save(chatSession);

        return ResponseEntity.status(HttpStatus.CREATED).body(chatMessage);
    }


    // 테스트용 채팅방 아이디 들어오면 관련 정보 주는 api
    @GetMapping("/{chatId}")
    public ResponseEntity<?> getChats(@PathVariable String chatId) {
        Chat chat = chatRepository.findById(chatId).orElseGet(null);
        return ResponseEntity.ok().body(chat);
    }


}
