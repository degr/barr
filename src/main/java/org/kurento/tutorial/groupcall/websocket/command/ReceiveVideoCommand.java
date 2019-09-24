package org.kurento.tutorial.groupcall.websocket.command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.kurento.tutorial.groupcall.services.UserRegistry;
import org.kurento.tutorial.groupcall.websocket.UserSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@AllArgsConstructor
@Component("receiveVideoFrom")
public class ReceiveVideoCommand implements RoomCommand {
    private static final String SENDER = "sender";
    private static final String SDP_OFFER = "sdpOffer";
    private final UserRegistry userRegistry;

    @SneakyThrows
    @Override
    public void execute(ObjectNode nodes, WebSocketSession socketSession) {
        UserSession userSession = userRegistry.getBySession(socketSession);
        if (userSession == null) {
            return;
        }
        final String senderName = nodes.get(SENDER).textValue();
        final UserSession senderUserSession = userRegistry.getByName(senderName);
        final String sdpOffer = nodes.get(SDP_OFFER).textValue();
        userSession.receiveVideoFrom(senderUserSession, sdpOffer);
    }
}