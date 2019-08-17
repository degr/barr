package com.scnsoft.permissions.service;

import com.scnsoft.permissions.converter.UserConverter;
import com.scnsoft.permissions.dto.UserDTO;
import com.scnsoft.permissions.persistence.entity.AdditionalPermission;
import com.scnsoft.permissions.persistence.entity.CompositePermissionId;
import com.scnsoft.permissions.persistence.entity.Permission;
import com.scnsoft.permissions.persistence.entity.User;
import com.scnsoft.permissions.persistence.repository.GroupRepository;
import com.scnsoft.permissions.persistence.repository.PermissionRepository;
import com.scnsoft.permissions.persistence.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService extends BaseCrudService<User, UserDTO, Long> {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final UserConverter converter;
    private final PermissionRepository permissionRepository;

    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository userRepository,
                       GroupRepository groupRepository,
                       UserConverter converter,
                       PermissionRepository permissionRepository, BCryptPasswordEncoder encoder) {
        super(userRepository, converter);
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.converter = converter;
        this.permissionRepository = permissionRepository;
        this.encoder = encoder;
    }

    public Optional<UserDTO> save(UserDTO userDTO) {
        return Optional.ofNullable(userDTO)
                .filter(userDTO1 -> !userRepository.existsByLogin(userDTO1.getLogin()))
                .map(userDTO1 -> {
                    userDTO1.setPassword(encoder.encode(userDTO.getPassword()));
                    saveEntity(userDTO1);
                    return userDTO1;
                });
    }

    public Optional<UserDTO> findByLogin(String name) {
        return userRepository.findUserByLogin(name)
                .map(converter::toDTO);
    }

    public void assignGroup(String login, String userGroupName) {
        userRepository.findUserByLogin(login)
                .ifPresent(user1 -> {
                    groupRepository.findUserGroupByName(userGroupName)
                            .ifPresent(user1::setGroup);
                    userRepository.save(user1);
                });
    }

    public void assignAdditionalPermission(String login, String permissionName, boolean isEnabled) {
        User user = userRepository.findUserByLogin(login).orElseThrow(RuntimeException::new);
        Permission permission = permissionRepository.findPermissionByName(permissionName)
                .orElseGet(() -> {
                    Permission newPermission = new Permission();
                    newPermission.setName(permissionName);
                    permissionRepository.save(newPermission);
                    return permissionRepository.findPermissionByName(permissionName)
                            .orElseThrow(RuntimeException::new);
                });
        user.getAdditionalPermissions()
                .add(AdditionalPermission.builder()
                        .id(new CompositePermissionId(user.getId(), permission.getId()))
                        .user(user)
                        .permission(permission)
                        .isEnabled(isEnabled)
                        .build()
                );
        userRepository.save(user);
    }

    @Override
    public List<UserDTO> findAll() {
        return entities().collect(Collectors.toList());
    }
}