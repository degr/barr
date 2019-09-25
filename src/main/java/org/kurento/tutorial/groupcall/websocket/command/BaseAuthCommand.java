package org.kurento.tutorial.groupcall.websocket.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.util.Strings;
import org.kurento.tutorial.groupcall.permissions.dto.UserDTO;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

abstract class BaseAuthCommand implements RoomCommand {
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";

    Map<Object, Object> authorize(ObjectNode sourceNode, Function<UserDTO, Map<Object, Object>> function) {
        UnaryOperator<String> operator = key -> Optional.ofNullable(sourceNode.get(key))
                .map(JsonNode::textValue)
                .orElse(Strings.EMPTY);
        String login = operator.apply(LOGIN);
        String password = operator.apply(PASSWORD);

        if (Strings.isEmpty(login) || Strings.isEmpty(password)) {
            return Collections.emptyMap();
        }
        UserDTO userDTO = UserDTO.builder().login(login).password(password).build();
        return function.apply(userDTO);
    }
}
