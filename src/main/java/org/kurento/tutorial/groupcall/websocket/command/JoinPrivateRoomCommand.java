package org.kurento.tutorial.groupcall.websocket.command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.codec.digest.DigestUtils;
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
    private final RoomManager roomManager;

    public JoinPrivateRoomCommand(UserRegistry userRegistry, RoomManager roomManager) {
        super(userRegistry);
        this.roomManager = roomManager;
    }

    @Override
    public void execute(@NotNull ObjectNode nodes, @NotNull WebSocketSession socketSession) {
        super.joinRoom(nodes, socketSession, key -> getOptionalRoom(key).orElseThrow(UnsupportedOperationException::new));
    }

    private Optional<Room> getOptionalRoom(String roomKey) {
        String encryptedRoomKey = DigestUtils.md5Hex(roomKey);
        Room room = roomManager.getRoom(encryptedRoomKey);
        if (room == null && isNotBlank(encryptedRoomKey)) {
            room = roomManager.createPrivateRoom(encryptedRoomKey);
        }
        return Objects.isNull(room) ? Optional.empty() : Optional.of(room);
    }
}