package org.kurento.tutorial.groupcall.websocket.command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.kurento.tutorial.groupcall.permissions.service.AuthenticationService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Component("signIn")
public class SignInCommand extends BaseAuthCommand implements RoomCommand {
    private static final String SIGN_IN = "signIn";
    private final AuthenticationService service;

    @SneakyThrows
    @Override
    public void execute(@NotNull ObjectNode node, @NotNull WebSocketSession socketSession) {
        JsonObject response = authorize(node, service::signIn);
        response.addProperty(BaseAuthCommand.ID, SIGN_IN);
        socketSession.sendMessage(new TextMessage(response.toString()));
    }
}