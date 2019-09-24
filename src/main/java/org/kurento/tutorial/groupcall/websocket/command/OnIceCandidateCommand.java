package org.kurento.tutorial.groupcall.websocket.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.kurento.client.IceCandidate;
import org.kurento.tutorial.groupcall.services.UserRegistry;
import org.kurento.tutorial.groupcall.websocket.UserSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@AllArgsConstructor
@Component("onIceCandidate")
public class OnIceCandidateCommand implements RoomCommand {
    private final UserRegistry userRegistry;
    private static final String NAME = "name";
    private static final String CANDIDATE = "candidate";
    private static final String SDP_MID = "sdpMid";
    private static final String SDP_MLINE_INDEX = "sdpMLineIndex";

    @SneakyThrows
    @Override
    public void execute(ObjectNode nodes, WebSocketSession socketSession) {
        UserSession userSession = userRegistry.getBySession(socketSession);
        if (userSession == null) {
            return;
        }
        String name = nodes.get(NAME).textValue();
        IceCandidate iceCandidate = parse(nodes.get(CANDIDATE));
        userSession.addCandidate(iceCandidate, name);
    }

    private IceCandidate parse(JsonNode jsonNode) {
        String candidateName = jsonNode.get(CANDIDATE).textValue();
        String sdpMid = jsonNode.get(SDP_MID).textValue();
        int sdpMLineIndex = jsonNode.get(SDP_MLINE_INDEX).asInt();
        return new IceCandidate(candidateName, sdpMid, sdpMLineIndex);
    }
}