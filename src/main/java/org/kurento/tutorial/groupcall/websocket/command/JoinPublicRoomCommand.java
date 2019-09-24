package org.kurento.tutorial.groupcall.websocket.command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import jdk.internal.joptsimple.internal.Strings;
import org.kurento.tutorial.groupcall.services.RoomManager;
import org.kurento.tutorial.groupcall.services.UserRegistry;
import org.kurento.tutorial.groupcall.websocket.Room;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.constraints.NotNull;

@Component("joinRoom")
public class JoinPublicRoomCommand extends JoinRoomCommand implements RoomCommand {
    private static final String LOGIN_KEY = "login";
    private static final String ROOM_KEY = "roomKey";
    private final RoomManager roomManager;

    public JoinPublicRoomCommand(UserRegistry userRegistry, RoomManager roomManager) {
        super(userRegistry);
        this.roomManager = roomManager;
    }

    @Override
    public void execute(@NotNull ObjectNode nodes, @NotNull WebSocketSession socketSession) {
        String login = nodes.get(LOGIN_KEY).textValue();
        String roomKey = nodes.get(ROOM_KEY).textValue();
        Room room = roomManager.getRoom(roomKey);
        super.joinRoom(login, Strings.EMPTY, socketSession, room);
    }
}