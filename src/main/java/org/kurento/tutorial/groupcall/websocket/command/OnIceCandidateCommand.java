package org.kurento.tutorial.groupcall.websocket.command;

import org.kurento.client.IceCandidate;
import org.kurento.tutorial.groupcall.dto.CandidateDto;
import org.kurento.tutorial.groupcall.dto.MessageDto;
import org.kurento.tutorial.groupcall.services.UserRegistry;
import org.kurento.tutorial.groupcall.websocket.UserSession;
import org.springframework.web.socket.WebSocketSession;

public class OnIceCandidateCommand implements RoomCommand {

    private final UserRegistry userRegistry;

    public OnIceCandidateCommand(UserRegistry userRegistry) {
        this.userRegistry = userRegistry;
    }

    @Override
    public void execute(MessageDto messageDto, WebSocketSession socketSession) {
        UserSession userSession = userRegistry.getBySession(socketSession);
        CandidateDto candidate = messageDto.getCandidate();
        if (userSession != null) {
            IceCandidate iceCandidate = new IceCandidate(candidate.getCandidate(),
                    candidate.getSdpMid(), candidate.getSdpMLineIndex());

            userSession.addCandidate(iceCandidate, messageDto.getName());
        }
    }
}
