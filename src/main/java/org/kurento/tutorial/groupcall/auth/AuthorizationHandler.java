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
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class AuthorizationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationHandler.class);
    private static final String BASE_URL = "http://localhost:8080/";
    private static final String LOGIN_KEY = "login";
    private static final String PASS_KEY = "password";
    private static final String TOKEN_KEY = "token";
    private static final String ANONYMOUS = "ANONYMOUS";
    private static final String EMPTY = "";

    public Map<String, String> authorize(String login, String password) {
        Function<String, Map<String, String>> singletonMapFunction = singleLogin -> getResponseMap(singleLogin, EMPTY);

        if (password.isEmpty()) {
            String userLogin = login.isEmpty() ? ANONYMOUS : login;
            return singletonMapFunction.apply(userLogin);
        }
        if (login.isEmpty()) {
            return singletonMapFunction.apply(ANONYMOUS);
        }
        JsonObject requestJson = new JsonObject();
        requestJson.addProperty(LOGIN_KEY, login);
        requestJson.addProperty(PASS_KEY, password);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        String path = BASE_URL + "users/signIn";
        String stringResponse;
        try {
            stringResponse = doPost(path, requestJson, headers);
        } catch (Exception e) {
            LOGGER.error("Unable to load user {}", String.valueOf(e));
            return singletonMapFunction.apply(login);
        }
        JsonObject jsonResponse = new Gson().fromJson(stringResponse, JsonObject.class);
        String responseLogin = jsonResponse.get(LOGIN_KEY).getAsString();
        String responseToken = jsonResponse.get(TOKEN_KEY).getAsString();
        return getResponseMap(responseLogin, responseToken);
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