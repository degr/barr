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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.kurento.tutorial.groupcall.services.RoomManager;
import org.kurento.tutorial.groupcall.services.UserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class CallHandler extends TextWebSocketHandler {
    private static final String ID = "id";
    private final RoomManager roomManager;
    private final UserRegistry sessionRegistry;
    private final CommandManager commandManager;

    @SneakyThrows
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        ObjectNode node = new ObjectMapper().readValue(message.getPayload(), ObjectNode.class);
        Optional.ofNullable(node.get(ID))
                .map(JsonNode::textValue)
                .flatMap(commandManager::getCommand)
                .ifPresent(roomCommand -> roomCommand.execute(node, session));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Optional.ofNullable(sessionRegistry.removeBySession(session))
                .ifPresent(user -> {
                    String roomName = user.getRoomKey();
                    Optional.ofNullable(roomManager.getRoom(roomName))
                            .ifPresent(room -> room.leave(user));
                });
    }
}