package com.example.demo;


//import com.example.demo.Component.WebSocketInterceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Configuration
@EnableWebSocketMessageBroker   // STOMP 사용
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    //private final WebSocketInterceptor webSocketInterceptor;
    @Override
    public void configureMessageBroker(final MessageBrokerRegistry registry) {
        // @Controller @MessageMapping 으로 라우팅
        registry.enableSimpleBroker("/sub");
        // 메시지 브로커로 라우팅
        registry.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws")     // 엔드포인트: /ws
                .setAllowedOriginPatterns("*");
        registry
                .addEndpoint("/ws")
                .setAllowedOriginPatterns("*")  // 엔드포인트: /ws
                .withSockJS();

    }
    /*@Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketInterceptor);
    }*/
}