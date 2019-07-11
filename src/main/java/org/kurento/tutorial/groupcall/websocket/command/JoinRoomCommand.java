package org.kurento.tutorial.groupcall.websocket.command;

import org.apache.commons.codec.digest.DigestUtils;
import org.kurento.tutorial.groupcall.auth.AuthorizationHandler;
import org.kurento.tutorial.groupcall.dto.MessageDto;
import org.kurento.tutorial.groupcall.services.RoomManager;
import org.kurento.tutorial.groupcall.services.UserRegistry;
import org.kurento.tutorial.groupcall.websocket.Room;
import org.kurento.tutorial.groupcall.websocket.UserSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Component
public class JoinRoomCommand implements RoomCommand {
    private static final String EMPTY = "";
    private final UserRegistry sessionRegistry;
    private final AuthorizationHandler authorizationHandler;
    private final RoomManager roomManager;

    public JoinRoomCommand(UserRegistry sessionRegistry, AuthorizationHandler authorizationHandler, RoomManager roomManager) {
        this.sessionRegistry = sessionRegistry;
        this.authorizationHandler = authorizationHandler;
        this.roomManager = roomManager;
    }

    @Override
    public void execute(MessageDto message, WebSocketSession webSocketSession) throws IOException {
        String key = message.getRoomKey();
        String roomKey = key == null ? EMPTY : key;

        String login = message.getName();
        String password = message.getPassword();
        String token = EMPTY;
        boolean isPrivate = Boolean.parseBoolean(message.getIsPrivateRoom());
        Room room;
        if (isPrivate) {
            room = getPrivateRoom(roomKey);
            token = authorizationHandler.authorize(login, password);
        } else {
            room = roomManager.getRoom(key);
        }
        UserSession userSession = new UserSession(login, token, room.getRoomKey(), webSocketSession, room.getMediaPipeline());
        try {
            room.join(userSession);
            sessionRegistry.register(userSession);
        } catch (Exception e) {
        }

    }

    private Room getPrivateRoom(String key) {
        Room room = roomManager.getRoom(DigestUtils.md5Hex(key.getBytes()));
        if (room == null) {
            room = roomManager.createPrivateRoom(key);
        }
        return room;
    }
}