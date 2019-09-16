package org.kurento.tutorial.groupcall.websocket.command;

import lombok.AllArgsConstructor;
import org.kurento.client.IceCandidate;
import org.kurento.tutorial.groupcall.dto.CandidateDto;
import org.kurento.tutorial.groupcall.dto.MessageDto;
import org.kurento.tutorial.groupcall.services.UserRegistry;
import org.kurento.tutorial.groupcall.websocket.UserSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@AllArgsConstructor
@Component("onIceCandidate")
public class OnIceCandidateCommand implements RoomCommand {
    private final UserRegistry userRegistry;

    @Override
    public void execute(MessageDto messageDto, WebSocketSession socketSession) {
        UserSession userSession = userRegistry.getBySession(socketSession);
        if (userSession == null) {
            return;
        }
        CandidateDto candidate = messageDto.getCandidate();
        IceCandidate iceCandidate = new IceCandidate(candidate.getCandidate(),
                candidate.getSdpMid(), candidate.getSdpMLineIndex());
        userSession.addCandidate(iceCandidate, messageDto.getName());
    }
}