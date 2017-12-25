package com.ecnu.trivia.configuration;

import com.ecnu.trivia.fliter.HttpSessionIdHandshakeInterceptor;
import com.ecnu.trivia.websocket.WebSocketServer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketServer(),"/websocket").addInterceptors(new HttpSessionIdHandshakeInterceptor()); //支持websocket 的访问链接
        registry.addHandler(new WebSocketServer(),"/sockjs/websocket").addInterceptors(new HttpSessionIdHandshakeInterceptor()).withSockJS(); //不支持websocket的访问链接
    }
}