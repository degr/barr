package org.kurento.tutorial.groupcall.websocket.command;

import org.kurento.tutorial.groupcall.dto.MessageDto;
import org.springframework.web.socket.WebSocketSession;

public interface RoomCommand {
    void execute(MessageDto messageDto, WebSocketSession socketSession);
}
