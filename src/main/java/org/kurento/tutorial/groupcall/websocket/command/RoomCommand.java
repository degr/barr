package org.kurento.tutorial.groupcall.websocket.command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.constraints.NotNull;

public interface RoomCommand {
    void execute(@NotNull ObjectNode node, @NotNull WebSocketSession socketSession);
}