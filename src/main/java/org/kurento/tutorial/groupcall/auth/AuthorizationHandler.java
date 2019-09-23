package org.kurento.tutorial.groupcall.auth;

import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.kurento.tutorial.groupcall.permissions.dto.UserDTO;
import org.kurento.tutorial.groupcall.permissions.service.AuthenticationService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@AllArgsConstructor
@Service
public class AuthorizationHandler {
    private static final String TOKEN = "token";
    private final AuthenticationService authenticationService;

    public String authorize(String login, String password) {
        Map<Object, Object> auth = auth(login, password, authenticationService::signIn);
        return String.valueOf(auth.get(TOKEN));
    }

    public Map<Object, Object> register(String login, String password) {
        return auth(login, password, authenticationService::signUp);
    }

    private Map<Object, Object> auth(String login, String password, Function<UserDTO, Map<Object, Object>> authenticator) {
        if (Strings.isBlank(login) || Strings.isBlank(password)) {
            return Collections.emptyMap();
        }
        UserDTO userDTO = UserDTO.builder().login(login).password(password).build();
        return Optional.of(userDTO)
                .map(authenticator)
                .orElse(Collections.emptyMap());
    }
}