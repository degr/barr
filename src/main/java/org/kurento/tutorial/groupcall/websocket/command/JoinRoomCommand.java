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
import java.util.Optional;

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
            String finalRoomKey = roomKey;
            room = Optional.ofNullable(roomManager.getRoom(DigestUtils.md5Hex(finalRoomKey.getBytes())))
                    .orElseGet(() -> roomManager.createPrivateRoom(finalRoomKey));
            roomKey = DigestUtils.md5Hex(roomKey.getBytes());

            token = authorizationHandler.authorize(login, password);
        } else {
            room = roomManager.getRoom(key);
        }
        UserSession userSession = new UserSession(login, token, roomKey, webSocketSession, room.getMediaPipeline());
        room.join(userSession);
        sessionRegistry.register(userSession);
    }
}