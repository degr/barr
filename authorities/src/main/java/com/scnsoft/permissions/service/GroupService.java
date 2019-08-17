package com.scnsoft.permissions.service;

import com.scnsoft.permissions.converter.GroupConverter;
import com.scnsoft.permissions.dto.GroupDTO;
import com.scnsoft.permissions.persistence.entity.Group;
import com.scnsoft.permissions.persistence.repository.GroupRepository;
import com.scnsoft.permissions.persistence.repository.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupService extends BaseCrudService<Group, GroupDTO, Long> {
    private final GroupRepository groupRepository;
    private final PermissionRepository permissionRepository;
    private final GroupConverter groupConverter;

    public GroupService(GroupRepository groupRepository,
                        PermissionRepository permissionRepository,
                        GroupConverter groupConverter) {
        super(groupRepository, groupConverter);
        this.groupRepository = groupRepository;
        this.permissionRepository = permissionRepository;
        this.groupConverter = groupConverter;
    }

    public Optional<GroupDTO> findByName(String userGroupName) {
        return groupRepository.findUserGroupByName(userGroupName)
                .map(groupConverter::toDTO);
    }

    public Optional<GroupDTO> assignPermission(String userGroupName, String permissionByName) {
        return groupRepository.findUserGroupByName(userGroupName)
                .map(userGroup1 -> {
                    permissionRepository.findPermissionByName(permissionByName)
                            .ifPresent(permission -> userGroup1.getPermissions().add(permission));
                    return groupRepository.save(userGroup1);
                }).map(groupConverter::toDTO);
    }

    @Override
    public List<GroupDTO> findAll() {
        return entities().collect(Collectors.toList());
    }
}