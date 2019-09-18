package org.kurento.tutorial.groupcall.websocket.command;

import org.kurento.tutorial.groupcall.dto.MessageDto;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.constraints.NotNull;

public interface RoomCommand {
    void execute(@NotNull MessageDto messageDto, @NotNull WebSocketSession socketSession);
}
