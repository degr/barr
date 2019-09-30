package org.kurento.tutorial.groupcall.websocket.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import org.kurento.tutorial.groupcall.services.UserRegistry;
import org.kurento.tutorial.groupcall.websocket.Room;
import org.kurento.tutorial.groupcall.websocket.SitPosition;
import org.kurento.tutorial.groupcall.websocket.UserSession;
import org.springframework.web.socket.WebSocketSession;

import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.UnaryOperator;

abstract class BaseJoinRoomCommand implements RoomCommand {
    private static final String LOGIN_KEY = "login";
    private static final String ROOM_KEY = "roomKey";
    private static final String TOKEN_KEY = "token";
    private static final String AXIS_X_KEY = "x";
    private static final String AXIS_Y_KEY = "y";
    private static final String AXIS_Z_KEY = "z";
    private static final String ROTATION_ANGLE_KEY = "a";
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
        SitPosition sitPosition = parseSitPosition(nodes.get("location"));
        Room room = roomExtractor.apply(roomKey);
        UserSession userSession = new UserSession(login, token, room.getRoomKey(), socketSession, room.getMediaPipeline());
        room.join(sitPosition, userSession);
        userRegistry.register(userSession);
    }

    private SitPosition parseSitPosition(JsonNode jsonNode) {
        if (jsonNode == null) {
            return null;
        }
        ToDoubleFunction<String> function = s -> jsonNode.get(s).asDouble();
        double x = function.applyAsDouble(AXIS_X_KEY);
        double y = function.applyAsDouble(AXIS_Y_KEY);
        double z = function.applyAsDouble(AXIS_Z_KEY);
        double a = function.applyAsDouble(ROTATION_ANGLE_KEY);
        return SitPosition.builder().axisX(x).axisY(y).axisZ(z).rotationAngle(a).build();
    }
}