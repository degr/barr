package org.kurento.tutorial.groupcall.websocket.command;

import org.kurento.tutorial.groupcall.dto.MessageDto;
import org.kurento.tutorial.groupcall.services.UserRegistry;
import org.kurento.tutorial.groupcall.websocket.UserSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Component
public class ReceiveVideoCommand implements RoomCommand {
    private final UserRegistry userRegistry;

    public ReceiveVideoCommand(UserRegistry userRegistry) {
        this.userRegistry = userRegistry;
    }

    @Override
    public void execute(MessageDto messageDto, WebSocketSession socketSession) throws IOException {
        UserSession userSession = userRegistry.getBySession(socketSession);
        if (userSession != null) {
            final String senderName = messageDto.getSender();
            final UserSession senderUserSession = userRegistry.getByName(senderName);
            final String sdpOffer = messageDto.getSdpOffer();
            userSession.receiveVideoFrom(senderUserSession, sdpOffer);
        }
    }
}