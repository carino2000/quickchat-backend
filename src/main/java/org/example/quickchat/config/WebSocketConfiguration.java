package org.example.quickchat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/*
    웹소켓은 디폴트가 1대1, 근데 너무 불편해서 생긴게 stomp!

    Stomp == 채널과 구독의 형태. 백에서 방송국과 그 내부에 다양한 채널을 만들고,
    거기에 메시지를 보내면 우릴 구독하고있는 클라이언트가 시청하는 느낌
 */

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins("*"); // /ws 로 들어오는 애들에 대해서 우린 웹소켓 형태를 취한다는 뜻
        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/quickchat"); // 라는 이름에 방송국이 생김. 엔드포인트가 /quickchat 으로 시작하면 다 채널
    }
}
