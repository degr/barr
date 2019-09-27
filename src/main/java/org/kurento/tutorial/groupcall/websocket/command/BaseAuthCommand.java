package org.kurento.tutorial.groupcall.websocket.command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.util.Strings;
import org.kurento.jsonrpc.JsonUtils;
import org.kurento.tutorial.groupcall.permissions.dto.UserDTO;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

abstract class BaseAuthCommand implements RoomCommand {
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    private static final String TOKEN = "token";
    private static final String PERMISSIONS = "permissions";
    private static final String PAYLOAD = "payload";
    static final String ID = "id";

    JsonObject authorize(ObjectNode node, Function<UserDTO, Map<Object, Object>> function) {
        Map<Object, Object> authMap = auth(node, function);
        return formJsonResponse(authMap);
    }

    private Map<Object, Object> auth(ObjectNode nodes, Function<UserDTO, Map<Object, Object>> function) {
        UnaryOperator<String> operator = valueExtractor(nodes);
        String login = operator.apply(LOGIN);
        String password = operator.apply(PASSWORD);

        if (Strings.isEmpty(login) || Strings.isEmpty(password)) {
            return Collections.emptyMap();
        }
        UserDTO userDTO = UserDTO.builder().login(login).password(password).build();
        return function.apply(userDTO);
    }

    private JsonObject formJsonResponse(Map<Object, Object> payloadMap) {
        JsonObject payloadObject = new JsonObject();
        Consumer<String> addToJson = key -> payloadObject.addProperty(key, JsonUtils.toJson(payloadMap.getOrDefault(key, null)));
        addToJson.accept(ID);
        addToJson.accept(LOGIN);
        addToJson.accept(TOKEN);
        Collection<String> strings = (Collection<String>) payloadMap.get(PERMISSIONS);
        JsonArray jsonArray = new JsonArray();
        strings.forEach(jsonArray::add);
        payloadObject.add(PERMISSIONS, jsonArray);
        JsonObject responseObject = new JsonObject();
        responseObject.add(PAYLOAD, payloadObject);
        return responseObject;
    }
}
