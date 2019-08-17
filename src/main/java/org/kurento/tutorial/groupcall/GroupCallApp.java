package org.kurento.tutorial.groupcall;

import org.kurento.client.KurentoClient;
import org.kurento.tutorial.groupcall.auth.AuthorizationHandler;
import org.kurento.tutorial.groupcall.services.RoomManager;
import org.kurento.tutorial.groupcall.services.UserRegistry;
import org.kurento.tutorial.groupcall.websocket.CallHandler;
import org.kurento.tutorial.groupcall.websocket.command.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@SpringBootApplication
@EnableWebSocket
public class GroupCallApp implements WebSocketConfigurer {
    @Bean
    public UserRegistry registry() {
        return new UserRegistry();
    }

    @Bean
    public RoomManager roomManager(KurentoClient kurento) {
        return new RoomManager(kurento);
    }

    @Bean
    public CallHandler groupCallHandler(RoomManager roomManager, UserRegistry registry) {
        return new CallHandler(roomManager, registry);
    }

    @Bean(name = "join")
    public RoomCommand joinRoomCommand(UserRegistry registry, AuthorizationHandler handler, RoomManager manager) {
        return new JoinRoomCommand(registry, handler, manager);
    }

    @Bean(name = "receive")
    public RoomCommand receiveVideo(UserRegistry registry) {
        return new ReceiveVideoCommand(registry);
    }

    @Bean(name = "leave")
    public RoomCommand leaveRoom(RoomManager manager, UserRegistry registry) {
        return new LeaveRoomCommand(manager, registry);
    }

    @Bean(name = "onIce")
    public RoomCommand onIceCandidate(UserRegistry registry) {
        return new OnIceCandidateCommand(registry);
    }

    @Bean
    public KurentoClient kurentoClient() {
        return KurentoClient.create();
    }

    public static void main(String[] args) {
        System.setProperty("kms.url", "ws://134.209.199.255:8888/kurento");
        SpringApplication.run(GroupCallApp.class, args);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(groupCallHandler(roomManager(kurentoClient()),
                registry()),
                "/groupcall");
    }
}