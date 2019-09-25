package org.kurento.tutorial.groupcall.websocket.command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.kurento.tutorial.groupcall.services.RoomManager;
import org.kurento.tutorial.groupcall.services.UserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.constraints.NotNull;

@Component("joinRoom")
public class JoinPublicRoomCommand extends JoinRoomCommand implements RoomCommand {
    private final RoomManager roomManager;

    public JoinPublicRoomCommand(UserRegistry userRegistry, RoomManager roomManager) {
        super(userRegistry);
        this.roomManager = roomManager;
    }

    @Override
    public void execute(@NotNull ObjectNode nodes, @NotNull WebSocketSession socketSession) {
        super.joinRoom(nodes, socketSession, roomManager::getRoom);
    }
}