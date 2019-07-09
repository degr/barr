package org.kurento.tutorial.groupcall.websocket.command;

import org.kurento.tutorial.groupcall.dto.MessageDto;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public interface RoomCommand {
    void execute(MessageDto messageDto, WebSocketSession socketSession) throws IOException;
}
