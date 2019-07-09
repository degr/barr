package org.kurento.tutorial.groupcall.websocket.command;

import org.kurento.tutorial.groupcall.dto.MessageDto;
import org.kurento.tutorial.groupcall.services.RoomManager;
import org.kurento.tutorial.groupcall.services.UserRegistry;
import org.kurento.tutorial.groupcall.websocket.Room;
import org.kurento.tutorial.groupcall.websocket.UserSession;
import org.springframework.web.socket.WebSocketSession;

public class LeaveRoomCommand implements RoomCommand {

    private final RoomManager roomManager;

    private final UserRegistry userRegistry;

    public LeaveRoomCommand(RoomManager roomManager, UserRegistry userRegistry) {
        this.roomManager = roomManager;
        this.userRegistry = userRegistry;
    }

    @Override
    public void execute(MessageDto messageDto, WebSocketSession socketSession) {
        UserSession userSession = userRegistry.getBySession(socketSession);
        final Room room = roomManager.getRoom(userSession.getRoomName());
        room.leave(userSession);
        if (room.getRoomParticipantsSessions().isEmpty()) {
            roomManager.removeRoom(room);
        }
    }
}
