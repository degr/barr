package org.kurento.tutorial.groupcall.websocket.command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import org.kurento.tutorial.groupcall.services.RoomManager;
import org.kurento.tutorial.groupcall.services.UserRegistry;
import org.kurento.tutorial.groupcall.websocket.Room;
import org.kurento.tutorial.groupcall.websocket.UserSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@AllArgsConstructor
@Component("leaveRoom")
public class LeaveRoomCommand implements RoomCommand {
    private final RoomManager roomManager;
    private final UserRegistry userRegistry;

    @Override
    public void execute(ObjectNode nodes, WebSocketSession socketSession) {
        UserSession userSession = userRegistry.getBySession(socketSession);
        if (userSession == null) {
            return;
        }
        final Room room = roomManager.getRoom(userSession.getRoomKey());
        room.leave(userSession);
        if (room.getRoomParticipantsSessions().isEmpty()) {
            roomManager.removeRoom(room);
        }
    }
}