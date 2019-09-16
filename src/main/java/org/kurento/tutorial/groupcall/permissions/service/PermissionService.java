package org.kurento.tutorial.groupcall.permissions.service;

import lombok.AllArgsConstructor;
import org.kurento.tutorial.groupcall.permissions.converter.EntityConverter;
import org.kurento.tutorial.groupcall.permissions.converter.GroupConverter;
import org.kurento.tutorial.groupcall.permissions.converter.UserConverter;
import org.kurento.tutorial.groupcall.permissions.dto.EntityDTO;
import org.kurento.tutorial.groupcall.permissions.dto.GroupDTO;
import org.kurento.tutorial.groupcall.permissions.dto.UserDTO;
import org.kurento.tutorial.groupcall.permissions.persistence.entity.*;
import org.kurento.tutorial.groupcall.permissions.persistence.repository.GroupRepository;
import org.kurento.tutorial.groupcall.permissions.persistence.repository.PermissionRepository;
import org.kurento.tutorial.groupcall.permissions.persistence.repository.UserRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.UnaryOperator;

@Service
@AllArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final GroupRepository groupRepository;
    private final GroupConverter groupConverter;

    public UserDTO assignPermissionToUser(Long userId, Long permissionId, boolean isEnabled) {
        UnaryOperator<User> assignPermissionAction = user -> {
            Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new NullPointerException("No such entity"));
            List<AdditionalPermission> additionalPermissions = user.getAdditionalPermissions();
            boolean contains = additionalPermissions.stream()
                    .anyMatch(additionalPermission -> (additionalPermission.getPermission().equals(permission)) && (additionalPermission.isEnabled() == isEnabled));
            if (contains) {
                return user;
            }
            additionalPermissions.add(
                    AdditionalPermission.builder()
                            .id(new CompositePermissionId(userId, permissionId))
                            .user(user)
                            .permission(permission)
                            .isEnabled(isEnabled)
                            .build()
            );
            return user;
        };
        return executePermissionAction(userRepository, userId, assignPermissionAction, userConverter);
    }

    public UserDTO releasePermissionFromUser(Long userId, Long permissionId) {
        UnaryOperator<User> releasePermissionAction = user -> {
            user.getAdditionalPermissions().removeIf(additionalPermission -> additionalPermission.getPermission().getId().equals(permissionId));
            return user;
        };
        return executePermissionAction(userRepository, userId, releasePermissionAction, userConverter);
    }

    public GroupDTO assignPermissionToGroup(Long groupId, Long permissionId) {
        UnaryOperator<Group> assignPermissionAction = group -> {
            Permission permission = permissionRepository.findById(permissionId).orElseThrow(() -> new NullPointerException("No such permission"));
            List<Permission> permissions = group.getPermissions();
            if (!permissions.contains(permission)) {
                permissions.add(permission);
            }
            return group;
        };
        return executePermissionAction(groupRepository, groupId, assignPermissionAction, groupConverter);
    }

    public GroupDTO releasePermissionFromGroup(Long groupId, Long permissionId) {
        UnaryOperator<Group> releasePermissionAction = group -> {
            group.getPermissions().removeIf(permission -> permission.getId().equals(permissionId));
            return group;
        };
        return executePermissionAction(groupRepository, groupId, releasePermissionAction, groupConverter);
    }

    private <T extends PersistenceEntity<Long>,
            K extends EntityDTO<Long>> K executePermissionAction(CrudRepository<T, Long> entityRepository, Long entityId,
                                                                 UnaryOperator<T> permissionAction,
                                                                 EntityConverter<T, K> converter) {
        return entityRepository.findById(entityId)
                .map(permissionAction)
                .map(entityRepository::save)
                .map(converter::toDTO)
                .orElseThrow(() -> new NullPointerException("No such entity"));
    }
}