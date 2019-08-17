package com.scnsoft.permissions.service;

import com.scnsoft.permissions.dto.UserDTO;
import com.scnsoft.permissions.security.jwt.JwtTokenProvider;
import com.scnsoft.permissions.security.jwt.JwtUserDetailsService;
import com.scnsoft.permissions.util.ExecutionTime;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuthenticationService {
    private static final String LOGIN_KEY = "login";
    private static final String TOKEN_KEY = "token";
    private final AuthenticationManager authenticationManager;
    private final JwtUserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public AuthenticationService(AuthenticationManager authenticationManager, JwtUserDetailsService userDetailsService, JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @ExecutionTime
    public Map<Object, Object> signIn(UserDTO userDTO) {
        String login = userDTO.getLogin();
        String password = userDTO.getPassword();
        return authenticate(login, password);
    }

    @ExecutionTime
    public Map<Object, Object> signUp(UserDTO userDTO) {
        String login = userDTO.getLogin();
        String password = userDTO.getPassword();
        userService.save(userDTO);
        return authenticate(login, password);
    }

    private Map<Object, Object> authenticate(String login, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, password));
        UserDetails userDetails = userDetailsService.loadUserByUsername(login);
        String token = jwtTokenProvider.createToken(userDetails);
        return Map.of(TOKEN_KEY, token, LOGIN_KEY, login);
    }
}