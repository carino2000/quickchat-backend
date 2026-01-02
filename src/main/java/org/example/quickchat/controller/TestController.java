package org.example.quickchat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestController {
    final SimpMessagingTemplate template;

    @GetMapping
    public ResponseEntity<?> handleGetTest(){

        template.convertAndSend("/quickchat/test", "Hello World");

        return ResponseEntity.ok().build();
    }

    @GetMapping("/advance")
    public ResponseEntity<?> handleGetAdvanceTest(){
        Map map = Map.of("type", "newMessage");
        template.convertAndSend("/quickchat/private", map);

        return ResponseEntity.ok().build();
    }

}
