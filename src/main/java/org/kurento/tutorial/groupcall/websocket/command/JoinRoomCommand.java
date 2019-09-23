package org.kurento.tutorial.groupcall.websocket.command;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.kurento.tutorial.groupcall.auth.AuthorizationHandler;
import org.kurento.tutorial.groupcall.dto.MessageDto;
import org.kurento.tutorial.groupcall.services.UserRegistry;
import org.kurento.tutorial.groupcall.websocket.Room;
import org.kurento.tutorial.groupcall.websocket.UserSession;
import org.springframework.web.socket.WebSocketSession;

@AllArgsConstructor
abstract class JoinRoomCommand {
    private final UserRegistry sessionRegistry;
    private final AuthorizationHandler authorizationHandler;

    @SneakyThrows
    void execute(MessageDto message, WebSocketSession webSocketSession, Room room) {
        String login = message.getName();
        String password = message.getPassword();
        String token = authorizationHandler.authorize(login, password);
        if (room == null) {
            return;
        }
        UserSession userSession = new UserSession(login, token, room.getRoomKey(), webSocketSession, room.getMediaPipeline());
        room.join(userSession);
        sessionRegistry.register(userSession);
    }
}