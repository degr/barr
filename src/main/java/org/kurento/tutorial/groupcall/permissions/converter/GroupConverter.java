package org.kurento.tutorial.groupcall.permissions.converter;

import lombok.RequiredArgsConstructor;
import org.kurento.tutorial.groupcall.permissions.dto.GroupDTO;
import org.kurento.tutorial.groupcall.permissions.persistence.entity.Group;
import org.kurento.tutorial.groupcall.permissions.persistence.entity.Permission;
import org.kurento.tutorial.groupcall.permissions.persistence.entity.User;
import org.kurento.tutorial.groupcall.permissions.persistence.repository.PermissionRepository;
import org.kurento.tutorial.groupcall.permissions.persistence.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GroupConverter implements EntityConverter<Group, GroupDTO> {
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    @Override
    public GroupDTO toDTO(Group entity) {
        return GroupDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .permissionNames(ConverterUtils.transform(entity.getPermissions(), Permission::getName))
                .userNames(ConverterUtils.transform(entity.getUsers(), User::getLogin))
                .build();
    }

    @Override
    public Group toPersistence(GroupDTO entityDTO) {
        Group group = new Group();
        Optional.ofNullable(entityDTO.getId()).ifPresent(group::setId);
        group.setName(entityDTO.getName());
        group.setPermissions(ConverterUtils.batchTransform(entityDTO.getPermissionNames(),
                permissionRepository::findPermissionsByNames));
        group.setUsers(ConverterUtils.batchTransform(entityDTO.getUserNames(),
                userRepository::findUsersByNames));
        return group;
    }
}