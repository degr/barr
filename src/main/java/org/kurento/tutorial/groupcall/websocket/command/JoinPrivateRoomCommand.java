package org.kurento.tutorial.groupcall.websocket.command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.codec.digest.DigestUtils;
import org.kurento.tutorial.groupcall.auth.AuthorizationHandler;
import org.kurento.tutorial.groupcall.services.RoomManager;
import org.kurento.tutorial.groupcall.services.UserRegistry;
import org.kurento.tutorial.groupcall.websocket.Room;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Optional;

import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Component("joinPrivateRoom")
public class JoinPrivateRoomCommand extends JoinRoomCommand implements RoomCommand {
    private static final String LOGIN_KEY = "login";
    private static final String ROOM_KEY = "roomKey";
    private static final String PASSWORD_KEY = "password";
    private final RoomManager roomManager;
    private final AuthorizationHandler authorizationHandler;

    public JoinPrivateRoomCommand(UserRegistry userRegistry, RoomManager roomManager, AuthorizationHandler authorizationHandler) {
        super(userRegistry);
        this.roomManager = roomManager;
        this.authorizationHandler = authorizationHandler;
    }

    @Override
    public void execute(@NotNull ObjectNode nodes, @NotNull WebSocketSession socketSession) {
        String login = nodes.get(LOGIN_KEY).textValue();
        String password = nodes.get(PASSWORD_KEY).textValue();
        String roomKey = DigestUtils.md5Hex(nodes.get(ROOM_KEY).textValue());
        String token = authorizationHandler.authorize(login, password);
        getOptionalRoom(roomKey).ifPresent(privateRoom -> super.joinRoom(login, token, socketSession, privateRoom));
    }

    private Optional<Room> getOptionalRoom(String roomKey) {
        Room room = roomManager.getRoom(roomKey);
        if (room == null && isNotBlank(roomKey)) {
            room = roomManager.createPrivateRoom(roomKey);
        }
        return Objects.isNull(room) ? Optional.empty() : Optional.of(room);
    }
}