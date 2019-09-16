package org.kurento.tutorial.groupcall.permissions.security.jwt;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.kurento.tutorial.groupcall.permissions.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.transaction.Transactional;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {
    private final UserService userService;
    private final JwtUserFactory jwtUserFactory;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        return Optional.ofNullable(username)
                .filter(Strings::isNotBlank)
                .flatMap(userService::findByLogin)
                .map(jwtUserFactory::build)
                .orElseThrow(() -> new UsernameNotFoundException("Unable to find user with such name"));
    }
}