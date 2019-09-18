package org.kurento.tutorial.groupcall.websocket.command;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.kurento.tutorial.groupcall.auth.AuthorizationHandler;
import org.kurento.tutorial.groupcall.dto.MessageDto;
import org.kurento.tutorial.groupcall.services.RoomManager;
import org.kurento.tutorial.groupcall.services.UserRegistry;
import org.kurento.tutorial.groupcall.websocket.Room;
import org.kurento.tutorial.groupcall.websocket.UserSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@AllArgsConstructor
@Component("joinRoom")
public class JoinRoomCommand implements RoomCommand {
    private final UserRegistry sessionRegistry;
    private final AuthorizationHandler authorizationHandler;
    private final RoomManager roomManager;

    @SneakyThrows
    @Override
    public void execute(MessageDto message, WebSocketSession webSocketSession) {
        String key = message.getRoomKey();
        String login = message.getName();
        String password = message.getPassword();
        boolean isPrivate = Boolean.parseBoolean(message.getIsPrivateRoom());
        String token;
        Room room;
        if (isPrivate) {
            token = authorizationHandler.authorize(login, password);
            room = getPrivateRoom(key);
        } else {
            token = EMPTY;
            room = getPublicRoom(key);
        }
        if (room == null) {
            return;
        }
        UserSession userSession = new UserSession(login, token, room.getRoomKey(), webSocketSession, room.getMediaPipeline());
        room.join(userSession);
        sessionRegistry.register(userSession);
    }

    private Room getPrivateRoom(String key) {
        Room room = roomManager.getRoom(DigestUtils.md5Hex(key.getBytes()));
        if (room == null && isNotBlank(key)) {
            room = roomManager.createPrivateRoom(key);
        } else if (room == null) {
            throw new UnsupportedOperationException("Unable to get private room with such key");
        }
        return room;
    }

    private Room getPublicRoom(String key) {
        return Optional.ofNullable(key)
                .map(roomManager::getRoom)
                .orElseThrow(() -> new UnsupportedOperationException("Unable to get public room with such key"));
    }
}