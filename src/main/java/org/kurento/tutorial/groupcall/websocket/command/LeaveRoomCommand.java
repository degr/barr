package org.kurento.tutorial.groupcall.websocket.command;

import lombok.AllArgsConstructor;
import org.kurento.tutorial.groupcall.dto.MessageDto;
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
    public void execute(MessageDto messageDto, WebSocketSession socketSession) {
        UserSession userSession = userRegistry.getBySession(socketSession);
        final Room room = roomManager.getRoom(userSession.getRoomKey());
        room.leave(userSession);
        if (room.getRoomParticipantsSessions().isEmpty()) {
            roomManager.removeRoom(room);
        }
    }
}