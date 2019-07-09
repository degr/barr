package org.kurento.tutorial.groupcall.websocket.command;

import org.kurento.tutorial.groupcall.auth.AuthorizationHandler;
import org.kurento.tutorial.groupcall.dto.MessageDto;
import org.kurento.tutorial.groupcall.services.RoomManager;
import org.kurento.tutorial.groupcall.services.UserRegistry;
import org.kurento.tutorial.groupcall.websocket.Room;
import org.kurento.tutorial.groupcall.websocket.UserSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

import static org.apache.logging.log4j.util.Strings.EMPTY;

@Component
public class JoinRoomCommand implements RoomCommand {
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    private static final String TOKEN = "token";

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
        String roomName = message.getRoom();
        String key = message.getSecretKey();
        String roomSecretKey = key != null ? key : EMPTY;
        int userLimit = Integer.parseInt(message.getUserNumber());
        boolean isPrivate = Boolean.parseBoolean(message.getIsPrivateRoom());
        Room room = getRoom(roomName, roomSecretKey, userLimit, isPrivate);

        String userName = message.getName();
        String password = message.getPassword();

        String login;
        String token;

        if (isPrivate) {
            Map<String, String> authInfo = authorizationHandler.authorize(userName, password);
            login = authInfo.get(LOGIN);
            token = authInfo.get(TOKEN);
        } else {
            login = userName;
            token = EMPTY;
        }

        UserSession userSession = new UserSession(login, token, roomName, webSocketSession, room.getMediaPipeline());
        userSession.setSecretRoomKey(roomSecretKey);

        room.join(userSession);
        sessionRegistry.register(userSession);
    }

    private Room getRoom(String roomName, String key, int userLimit, boolean isPrivate) {
        Room room = roomManager.getRoom(roomName);
        if (room == null) {
            if (isPrivate) {
                room = roomManager.createPrivateRoom(roomName, key);
            } else {
                room = roomManager.createRoom(roomName, userLimit);
            }
        }
        return room;
    }
}

