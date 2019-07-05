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
import java.util.Optional;


@Slf4j
public class CallHandler extends TextWebSocketHandler {
    private static final String CREATE_ROOM = "createRoom";
    private static final String JOIN_ROOM = "joinRoom";
    private static final String RECEIVE_VIDEO_FROM = "receiveVideoFrom";
    private static final String LEAVE_ROOM = "leaveRoom";
    private static final String ON_ICE_CANDIDATE = "onIceCandidate";
    private final RoomManager roomManager;

    private final UserRegistry sessionRegistry;

    public CallHandler(RoomManager roomManager, UserRegistry sessionRegistry) {
        this.roomManager = roomManager;
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public void handleTextMessage(WebSocketSession webSocketSession, TextMessage message) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        MessageDto messageDto = mapper.readValue(message.getPayload(), MessageDto.class);
        UserSession userSession = sessionRegistry.getBySession(webSocketSession);

        switch (messageDto.getId()) {
            case JOIN_ROOM:
                joinRoom(messageDto, webSocketSession);
                break;
            case RECEIVE_VIDEO_FROM:
                if (userSession != null) {
                    final String senderName = messageDto.getSender();
                    final UserSession senderUserSession = sessionRegistry.getByName(senderName);
                    final String sdpOffer = messageDto.getSdpOffer();
                    userSession.receiveVideoFrom(senderUserSession, sdpOffer);
                }
                break;
            case LEAVE_ROOM:
                Optional.ofNullable(userSession).ifPresent(this::leaveRoom);
                break;
            case ON_ICE_CANDIDATE:
                CandidateDto candidate = messageDto.getCandidate();
                if (userSession != null) {
                    IceCandidate iceCandidate = new IceCandidate(candidate.getCandidate(),
                            candidate.getSdpMid(), candidate.getSdpMLineIndex());

                    userSession.addCandidate(iceCandidate, messageDto.getName());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        UserSession user = sessionRegistry.removeBySession(session);
        roomManager.getRoom(user.getRoomName()).leave(user);
    }
    /*private void createRoom()*/


    private void joinRoom(MessageDto params, WebSocketSession webSocketSession) throws IOException {
        final String userName = params.getName();
        final String roomName = params.getRoom();
        Room room = Optional.ofNullable(roomManager.getRoom(roomName))
                .orElseGet(() -> roomManager.create(roomName));

        UserSession userSession = new UserSession(userName, roomName, webSocketSession, room.getMediaPipeline());
        room.join(userSession);
        sessionRegistry.register(userSession);
    }

    private void leaveRoom(UserSession user) {
        final Room room = roomManager.getRoom(user.getRoomName());
        room.leave(user);
        if (room.getRoomParticipantsSessions().isEmpty()) {
            roomManager.removeRoom(room);
        }
    }
}
