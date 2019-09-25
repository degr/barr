package org.kurento.tutorial.groupcall.permissions.service;

import lombok.RequiredArgsConstructor;
import org.kurento.tutorial.groupcall.permissions.dto.UserDTO;
import org.kurento.tutorial.groupcall.permissions.security.jwt.JwtTokenProvider;
import org.kurento.tutorial.groupcall.permissions.security.jwt.JwtUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);
    private static final String ID_KEY = "id";
    private static final String LOGIN_KEY = "login";
    private static final String TOKEN_KEY = "token";
    private static final String PERMISSIONS_KEY = "permissions";
    private static final String PATIENT_PERMISSION = "PATIENT";
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public Map<Object, Object> signIn(UserDTO userDTO) {
        String login = userDTO.getLogin();
        String password = userDTO.getPassword();
        return authenticate(login, password);
    }

    public Map<Object, Object> signUp(UserDTO userDTO) {
        String login = userDTO.getLogin();
        if (userService.existByLogin(login)) {
            LOGGER.error("Unable to sign up. User with name: \"{}\" is already exist.", login);
            return Collections.emptyMap();
        }
        String password = userDTO.getPassword();
        userDTO.setPermissions(getValidPermissions(userDTO.getPermissions()));
        userService.save(userDTO);
        return authenticate(login, password);
    }

    private Map<Object, Object> authenticate(String login, String password) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, password));
        JwtUser principal = (JwtUser) authenticate.getPrincipal();
        Long id = principal.getId();
        String username = principal.getUsername();
        Collection<String> roleNames = authenticate.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        String token = jwtTokenProvider.createToken(username, roleNames);
        Map<Object, Object> map = new HashMap<>();
        map.put(ID_KEY, id);
        map.put(LOGIN_KEY, username);
        map.put(PERMISSIONS_KEY, roleNames);
        map.put(TOKEN_KEY, token);
        return Collections.unmodifiableMap(map);
    }

    private List<String> getValidPermissions(List<String> inputPermissions) {
        if (CollectionUtils.isEmpty(inputPermissions)) {
            return Collections.singletonList(PATIENT_PERMISSION);
        }
        if (!inputPermissions.contains(PATIENT_PERMISSION)) {
            inputPermissions.add(PATIENT_PERMISSION);
        }
        return inputPermissions;
    }
}