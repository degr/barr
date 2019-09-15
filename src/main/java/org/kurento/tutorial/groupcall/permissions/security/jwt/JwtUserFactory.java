package org.kurento.tutorial.groupcall.permissions.security.jwt;

import org.kurento.tutorial.groupcall.permissions.dto.GroupDTO;
import org.kurento.tutorial.groupcall.permissions.dto.UserDTO;
import org.kurento.tutorial.groupcall.permissions.service.GroupService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class JwtUserFactory {

    private final GroupService groupService;

    public JwtUserFactory(GroupService groupService) {
        this.groupService = groupService;
    }

    public JwtUser build(UserDTO userDTO) {
        return JwtUser.builder()
                .id(userDTO.getId())
                .username(userDTO.getLogin())
                .password(userDTO.getPassword())
                .isEnabled(true)
                .authorities(getAvailableUserAuthorities(userDTO))
                .build();
    }

    private Collection<SimpleGrantedAuthority> getAvailableUserAuthorities(UserDTO userDTO) {
        Map<String, Boolean> additionalPermissionMap = Optional.ofNullable(userDTO.getAdditionalPermissions())
                .orElse(Collections.emptyMap());

        Collection<String> userPermissionsNames = groupService.findByName(userDTO.getGroupName())
                .map(GroupDTO::getPermissionNames)
                .map(groupPermissionNames -> {
                    groupPermissionNames.addAll(additionalPermissionMap.keySet());
                    return groupPermissionNames;
                })
                .filter(list -> !list.isEmpty())
                .orElse(Collections.emptyList());

        return userPermissionsNames
                .stream()
                .filter(permissionName -> isPermissionSupported(permissionName, additionalPermissionMap))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    private boolean isPermissionSupported(String permissionName, Map<String, Boolean> additionalPermissions) {
        for (Map.Entry<String, Boolean> permissionEntry : additionalPermissions.entrySet()) {
            if (permissionEntry.getKey().equals(permissionName)) {
                return permissionEntry.getValue();
            }
        }
        return true;
    }
}