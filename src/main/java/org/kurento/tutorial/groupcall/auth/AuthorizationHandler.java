package org.kurento.tutorial.groupcall.auth;

import com.google.gson.JsonObject;
import org.kurento.tutorial.groupcall.permissions.dto.UserDTO;
import org.kurento.tutorial.groupcall.permissions.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthorizationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationHandler.class);

    private final AuthenticationService authenticationService;

    public AuthorizationHandler(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }


    public String authorize(String login, String password) {
        if (password.isEmpty() || login.isEmpty()) {
            return "";
        }

        JsonObject requestJson = new JsonObject();
        requestJson.addProperty("login", login);
        requestJson.addProperty("password", password);


        Map<Object, Object> stringResponse;
        try {
            stringResponse = authenticationService.signIn(UserDTO.builder().login(login).password(password).build());
            return (String) stringResponse.get("token");
        } catch (Exception e) {
            LOGGER.error("Unable to load user {}", String.valueOf(e));
            return "";
        }

    }

}