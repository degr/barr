package org.kurento.tutorial.groupcall.auth;

import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.kurento.tutorial.groupcall.permissions.dto.UserDTO;
import org.kurento.tutorial.groupcall.permissions.service.AuthenticationService;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.apache.logging.log4j.util.Strings.EMPTY;

@AllArgsConstructor
@Service
public class AuthorizationHandler {
    private static final String TOKEN = "token";
    private final AuthenticationService authenticationService;

    public String authorize(String login, String password) {
        if (Strings.isBlank(login) || Strings.isBlank(password)) {
            return EMPTY;
        }
        UserDTO userDTO = UserDTO.builder().login(login).password(password).build();
        return Optional.of(userDTO)
                .map(authenticationService::signIn)
                .map(map -> map.get(TOKEN))
                .map(String::valueOf)
                .orElse(EMPTY);
    }
}