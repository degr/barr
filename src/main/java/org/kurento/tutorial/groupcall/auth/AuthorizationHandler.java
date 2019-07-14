package org.kurento.tutorial.groupcall.auth;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

public class AuthorizationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationHandler.class);
    private static final String BASE_URL = "http://localhost:8080/";
    private static final String LOGIN_KEY = "login";
    private static final String PASS_KEY = "password";
    private static final String TOKEN_KEY = "token";
    private static final String EMPTY = "";
    private static final String SIGN_IN_URL = "users/signIn";

    public String authorize(String login, String password) {
        if (password.isEmpty() || login.isEmpty()) {
            return EMPTY;
        }

        JsonObject requestJson = new JsonObject();
        requestJson.addProperty(LOGIN_KEY, login);
        requestJson.addProperty(PASS_KEY, password);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);


        String path = BASE_URL + SIGN_IN_URL;
        String stringResponse;
        try {
            stringResponse = doPost(path, requestJson, headers);
        } catch (Exception e) {
            LOGGER.error("Unable to load user {}", String.valueOf(e));
            return EMPTY;
        }
        JsonObject jsonResponse = new Gson().fromJson(stringResponse, JsonObject.class);
        return jsonResponse.get(TOKEN_KEY).getAsString();
    }

    private String doPost(String path, JsonObject jsonObject, HttpHeaders httpHeaders) {
        HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), httpHeaders);
        return new RestTemplate().postForObject(path, entity, String.class);
    }
}