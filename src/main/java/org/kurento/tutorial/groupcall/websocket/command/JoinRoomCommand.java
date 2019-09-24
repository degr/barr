package org.kurento.tutorial.groupcall.websocket.command;

import lombok.SneakyThrows;
import org.kurento.tutorial.groupcall.services.UserRegistry;
import org.kurento.tutorial.groupcall.websocket.Room;
import org.kurento.tutorial.groupcall.websocket.UserSession;
import org.springframework.web.socket.WebSocketSession;

abstract class JoinRoomCommand {
    private final UserRegistry userRegistry;

    public JoinRoomCommand(UserRegistry userRegistry) {
        this.userRegistry = userRegistry;
    }

    @SneakyThrows
    void joinRoom(String login, String token, WebSocketSession socketSession, Room room) {
        UserSession userSession = new UserSession(login, token, room.getRoomKey(), socketSession, room.getMediaPipeline());
        room.join(userSession);
        userRegistry.register(userSession);
    }
}