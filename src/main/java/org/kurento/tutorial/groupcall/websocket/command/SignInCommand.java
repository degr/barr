package org.kurento.tutorial.groupcall.websocket.command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.kurento.tutorial.groupcall.permissions.service.AuthenticationService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.constraints.NotNull;
import java.util.Map;

@AllArgsConstructor
@Component("signIn")
public class SignInCommand extends BaseAuthCommand implements RoomCommand {
    private final AuthenticationService service;

    @SneakyThrows
    @Override
    public void execute(@NotNull ObjectNode node, @NotNull WebSocketSession socketSession) {
        Map<Object, Object> authorize = authorize(node, service::signIn);
        socketSession.sendMessage(new TextMessage(authorize.toString()));
    }
}