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
import org.kurento.tutorial.groupcall.dto.MessageDto;
import org.kurento.tutorial.groupcall.services.RoomManager;
import org.kurento.tutorial.groupcall.services.UserRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Optional;

@Slf4j
public class CallHandler extends TextWebSocketHandler {
    private final RoomManager roomManager;
    private final UserRegistry sessionRegistry;
    @Autowired
    private CommandManager commandManager;

    public CallHandler(RoomManager roomManager, UserRegistry sessionRegistry) {
        this.roomManager = roomManager;
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        MessageDto messageDto = objectMapper.readValue(message.getPayload(), MessageDto.class);
        Optional.ofNullable(messageDto.getId())
                .flatMap(commandManager::getCommand)
                .ifPresent(roomCommand -> roomCommand.execute(messageDto, session));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        UserSession user = sessionRegistry.removeBySession(session);
        String roomName = user.getRoomKey();
        Optional.ofNullable(roomManager.getRoom(roomName))
                .ifPresent(room -> room.leave(user));
    }
}