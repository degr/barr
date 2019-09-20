package org.kurento.tutorial.groupcall.websocket.command;

import org.kurento.tutorial.groupcall.auth.AuthorizationHandler;
import org.kurento.tutorial.groupcall.dto.MessageDto;
import org.kurento.tutorial.groupcall.services.RoomManager;
import org.kurento.tutorial.groupcall.services.UserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Component("joinRoom")
public class JoinPublicRoomCommand extends JoinRoomCommand implements RoomCommand {
    private final RoomManager roomManager;

    public JoinPublicRoomCommand(UserRegistry sessionRegistry,
                                 AuthorizationHandler authorizationHandler,
                                 RoomManager roomManager) {
        super(sessionRegistry, authorizationHandler);
        this.roomManager = roomManager;
    }

    @Override
    public void execute(@NotNull MessageDto messageDto, @NotNull WebSocketSession socketSession) {
        Optional.ofNullable(messageDto.getRoomKey())
                .map(roomManager::getRoom)
                .ifPresent(room -> super.execute(messageDto, socketSession, room));
    }
}