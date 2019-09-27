package org.kurento.tutorial.groupcall.websocket.command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import org.kurento.tutorial.groupcall.services.UserRegistry;
import org.kurento.tutorial.groupcall.websocket.Room;
import org.kurento.tutorial.groupcall.websocket.UserSession;
import org.springframework.web.socket.WebSocketSession;

import java.util.function.Function;
import java.util.function.UnaryOperator;

abstract class BaseJoinRoomCommand implements RoomCommand {
    private static final String LOGIN_KEY = "login";
    private static final String ROOM_KEY = "roomKey";
    private static final String TOKEN_KEY = "token";
    private final UserRegistry userRegistry;

    BaseJoinRoomCommand(UserRegistry userRegistry) {
        this.userRegistry = userRegistry;
    }

    @SneakyThrows
    void joinRoom(ObjectNode nodes, WebSocketSession socketSession, Function<String, Room> roomExtractor) {
        UnaryOperator<String> operator = valueExtractor(nodes);
        String login = operator.apply(LOGIN_KEY);
        String token = operator.apply(TOKEN_KEY);
        String roomKey = operator.apply(ROOM_KEY);

        Room room = roomExtractor.apply(roomKey);
        UserSession userSession = new UserSession(login, token, room.getRoomKey(), socketSession, room.getMediaPipeline());
        room.join(userSession);
        userRegistry.register(userSession);
    }
}