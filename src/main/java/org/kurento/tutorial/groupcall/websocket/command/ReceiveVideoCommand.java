package org.kurento.tutorial.groupcall.websocket.command;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.kurento.tutorial.groupcall.dto.MessageDto;
import org.kurento.tutorial.groupcall.services.UserRegistry;
import org.kurento.tutorial.groupcall.websocket.UserSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@AllArgsConstructor
@Component("receiveVideoFrom")
public class ReceiveVideoCommand implements RoomCommand {
    private final UserRegistry userRegistry;

    @SneakyThrows
    @Override
    public void execute(MessageDto messageDto, WebSocketSession socketSession) {
        UserSession userSession = userRegistry.getBySession(socketSession);
        if (userSession == null) {
            return;
        }
        final String senderName = messageDto.getSender();
        final UserSession senderUserSession = userRegistry.getByName(senderName);
        final String sdpOffer = messageDto.getSdpOffer();
        userSession.receiveVideoFrom(senderUserSession, sdpOffer);
    }
}