package org.kurento.tutorial.groupcall.websocket.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jdk.internal.joptsimple.internal.Strings;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.function.UnaryOperator;

public interface RoomCommand {
    void execute(@NotNull ObjectNode node, @NotNull WebSocketSession socketSession);

    default UnaryOperator<String> valueExtractor(JsonNode jsonNode) {
        return s -> Optional.ofNullable(jsonNode.get(s)).map(JsonNode::textValue).orElse(Strings.EMPTY);
    }
}