package org.kurento.tutorial.groupcall;

import lombok.AllArgsConstructor;
import org.kurento.tutorial.groupcall.websocket.CallHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@SpringBootApplication
@EnableWebSocket
@AllArgsConstructor
public class GroupCallApp implements WebSocketConfigurer {
    private final CallHandler callHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(callHandler, "/groupcall").setAllowedOrigins("*");
    }

    public static void main(String[] args) {
        String key = "kms.url";
        String value = "ws://134.209.199.255:8888/kurento";
        System.setProperty(key, value);
        SpringApplication.run(GroupCallApp.class, args);
    }
}