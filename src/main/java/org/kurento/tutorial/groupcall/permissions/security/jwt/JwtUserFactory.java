package org.kurento.tutorial.groupcall.permissions.security.jwt;

import org.kurento.tutorial.groupcall.permissions.dto.UserDTO;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class JwtUserFactory {
    public JwtUser build(UserDTO userDTO) {
        return JwtUser.builder()
                .id(userDTO.getId())
                .username(userDTO.getLogin())
                .password(userDTO.getPassword())
                .authorities(toAuthorities(userDTO.getPermissions()))
                .build();
    }

    private Collection<SimpleGrantedAuthority> toAuthorities(List<String> permissions) {
        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}