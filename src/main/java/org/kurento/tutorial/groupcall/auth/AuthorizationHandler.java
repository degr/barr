package org.kurento.tutorial.groupcall.auth;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class AuthorizationHandler {
    private static final String BASE_URL = "http://localhost:8080/";
    private static final String LOGIN_KEY = "login";
    private static final String PASSWORD_KEY = "password";
    private static final String TOKEN_KEY = "token";
    private static final String ANONYMOUS = "ANONYMOUS";
    private static final String EMPTY = "";

    private Optional<String> doGet(String path) {
        RestTemplate template = new RestTemplate();
        String fullPath = BASE_URL + path;
        return Optional.ofNullable(template.getForEntity(fullPath, String.class).getBody());
    }

    public Map<String, String> authorize(JsonObject map, HttpHeaders headers) {
        Function<String, Map<String, String>> function = login -> getResponseMap(login, EMPTY);
        JsonElement jsonLogin = map.get(LOGIN_KEY);
        JsonElement jsonPassword = map.get(PASSWORD_KEY);
        if (jsonLogin.isJsonNull() || jsonPassword.isJsonNull()) {
            String nonEmptyLogin = jsonLogin.isJsonNull() ? ANONYMOUS : jsonLogin.getAsString();
            return function.apply(nonEmptyLogin);
        }
        String path = BASE_URL + "users/signIn";
        String stringResponse;
        try {
            stringResponse = doPost(path, map, headers);
        } catch (Exception e) {
            return function.apply(jsonLogin.getAsString());
        }
        JsonObject jsonObject = new Gson().fromJson(stringResponse, JsonObject.class);
        String login = jsonObject.get(LOGIN_KEY).getAsString();
        String token = jsonObject.get(TOKEN_KEY).getAsString();

        return getResponseMap(login, token);
    }

    private Map<String, String> getResponseMap(String login, String token) {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put(LOGIN_KEY, login);
        responseMap.put(TOKEN_KEY, token);
        return Collections.unmodifiableMap(responseMap);
    }

    private String doPost(String path, JsonObject jsonObject, HttpHeaders httpHeaders) {
        HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), httpHeaders);
        return new RestTemplate().postForObject(path, entity, String.class);
    }
}
