package org.kurento.tutorial.groupcall.websocket.command;

import org.apache.commons.codec.digest.DigestUtils;
import org.kurento.tutorial.groupcall.auth.AuthorizationHandler;
import org.kurento.tutorial.groupcall.dto.MessageDto;
import org.kurento.tutorial.groupcall.services.RoomManager;
import org.kurento.tutorial.groupcall.services.UserRegistry;
import org.kurento.tutorial.groupcall.websocket.Room;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.constraints.NotNull;

import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Component("joinPrivateRoom")
public class JoinPrivateRoomCommand extends JoinRoomCommand implements RoomCommand {
    private final RoomManager roomManager;

    public JoinPrivateRoomCommand(UserRegistry sessionRegistry,
                                  AuthorizationHandler authorizationHandler,
                                  RoomManager roomManager) {
        super(sessionRegistry, authorizationHandler);
        this.roomManager = roomManager;
    }

    @Override
    public void execute(@NotNull MessageDto messageDto, @NotNull WebSocketSession socketSession) {
        String roomKey = messageDto.getRoomKey();
        String encoded = DigestUtils.md5Hex(roomKey);
        Room room = roomManager.getRoom(encoded);
        if (room == null && isNotBlank(encoded)) {
            room = roomManager.createPrivateRoom(encoded);
        } else if (room == null) {
            throw new UnsupportedOperationException("Unable to get private room with such key");
        }
        super.execute(messageDto, socketSession, room);
    }
}