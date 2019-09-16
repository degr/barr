package org.kurento.tutorial.groupcall.permissions.converter;

import lombok.RequiredArgsConstructor;
import org.kurento.tutorial.groupcall.permissions.dto.UserDTO;
import org.kurento.tutorial.groupcall.permissions.persistence.entity.AdditionalPermission;
import org.kurento.tutorial.groupcall.permissions.persistence.entity.Group;
import org.kurento.tutorial.groupcall.permissions.persistence.entity.Permission;
import org.kurento.tutorial.groupcall.permissions.persistence.entity.User;
import org.kurento.tutorial.groupcall.permissions.persistence.repository.GroupRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Component
@RequiredArgsConstructor
public class UserConverter implements EntityConverter<User, UserDTO> {
    private final GroupRepository groupRepository;

    @Override
    public UserDTO toDTO(User entity) {
        Optional<Group> optionalGroup = Optional.ofNullable(entity.getGroup());
        String groupName = optionalGroup.map(Group::getName).orElse(null);

        List<Permission> groupPermissions = optionalGroup.map(Group::getPermissions).orElse(Collections.emptyList());
        List<AdditionalPermission> additionalPermissions = Optional.ofNullable(entity.getAdditionalPermissions()).orElse(Collections.emptyList());
        List<String> permissions = merge(groupPermissions, additionalPermissions);

        return UserDTO.builder()
                .id(entity.getId())
                .login(entity.getLogin())
                .password(entity.getPassword())
                .groupName(groupName)
                .permissions(permissions)
                .build();
    }

    private List<String> merge(List<Permission> groupPermissions, List<AdditionalPermission> additionalPermissions) {
        Map<String, Boolean> resolvedAdditionalPermissions = additionalPermissions.stream()
                .collect(toMap(additionalPermission -> additionalPermission.getPermission().getName(), AdditionalPermission::isEnabled));
        return Stream.concat(resolvedAdditionalPermissions.keySet().stream(), groupPermissions.stream().map(Permission::getName))
                .distinct()
                .filter(permission -> resolvedAdditionalPermissions.getOrDefault(permission, true))
                .collect(toList());
    }

    @Override
    public User toPersistence(UserDTO entityDTO) {
        User user = new User();
        Optional.ofNullable(entityDTO.getId())
                .ifPresent(user::setId);

        user.setLogin(requireNonNull(entityDTO.getLogin()));
        user.setPassword(requireNonNull(entityDTO.getPassword()));

        Optional.ofNullable(entityDTO.getGroupName())
                .filter(s -> !s.trim().isEmpty())
                .flatMap(groupRepository::findGroupByName)
                .ifPresent(user::setGroup);
        return user;
    }
}