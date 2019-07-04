/*
 * (C) Copyright 2014 Kurento (http://kurento.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.kurento.tutorial.groupcall.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.IceCandidate;
import org.kurento.tutorial.groupcall.dto.CandidateDto;
import org.kurento.tutorial.groupcall.dto.MessageDto;
import org.kurento.tutorial.groupcall.services.RoomManager;
import org.kurento.tutorial.groupcall.services.UserRegistry;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;


@Slf4j
public class CallHandler extends TextWebSocketHandler {

    private final RoomManager roomManager;

    private final UserRegistry registry;

    public CallHandler(RoomManager roomManager, UserRegistry registry) {
        this.roomManager = roomManager;
        this.registry = registry;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        MessageDto jsonMessage = mapper.readValue(message.getPayload(), MessageDto.class);


        final UserSession user = registry.getBySession(session);

        if (user != null) {
            log.debug("Incoming message from user '{}': {}", user.getName(), jsonMessage);
        } else {
            log.debug("Incoming message from new user: {}", jsonMessage);

        }

        switch (jsonMessage.getId()) {
            case "joinRoom":
                joinRoom(jsonMessage, session);
                break;
            case "receiveVideoFrom":
                if (user != null) {
                    final String senderName = jsonMessage.getSender();
                    final UserSession sender = registry.getByName(senderName);
                    final String sdpOffer = jsonMessage.getSdpOffer();
                    user.receiveVideoFrom(sender, sdpOffer);
                } else {
                    log.error("Trying to receiveVideoFrom, but no user");
                }
                break;
            case "leaveRoom":
                if (user != null) {
                    leaveRoom(user);
                } else {
                    log.error("trying to leave room, but no user");
                }
                break;
            case "onIceCandidate":
                CandidateDto candidate = jsonMessage.getCandidate();

                if (user != null) {
                    IceCandidate iceCandidate = new IceCandidate(candidate.getCandidate(),
                            candidate.getSdpMid(), candidate.getSdpMLineIndex());
                    user.addCandidate(iceCandidate, jsonMessage.getName());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        UserSession user = registry.removeBySession(session);
        roomManager.getRoom(user.getRoomName()).leave(user);
    }

    private void joinRoom(MessageDto params, WebSocketSession session) throws IOException {
        final String roomName = params.getRoom();
        final String name = params.getName();
        log.info("PARTICIPANT {}: trying to join room {}", name, roomName);
        Room room = roomManager.getRoom(roomName);
        final UserSession user = room.join(name, session);
        registry.register(user);
    }

    private void leaveRoom(UserSession user) {
        final Room room = roomManager.getRoom(user.getRoomName());
        room.leave(user);
        if (room.getParticipants().isEmpty()) {
            roomManager.removeRoom(room);
        }
    }
}
