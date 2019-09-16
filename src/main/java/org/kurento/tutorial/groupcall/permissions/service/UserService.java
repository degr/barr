package org.kurento.tutorial.groupcall.permissions.service;

import org.apache.logging.log4j.util.Strings;
import org.kurento.tutorial.groupcall.permissions.converter.UserConverter;
import org.kurento.tutorial.groupcall.permissions.dto.UserDTO;
import org.kurento.tutorial.groupcall.permissions.persistence.entity.Group;
import org.kurento.tutorial.groupcall.permissions.persistence.entity.User;
import org.kurento.tutorial.groupcall.permissions.persistence.repository.GroupRepository;
import org.kurento.tutorial.groupcall.permissions.persistence.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService extends BaseCrudService<User, UserDTO, Long> {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final UserConverter userConverter;
    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository userRepository,
                       GroupRepository groupRepository,
                       UserConverter userConverter,
                       BCryptPasswordEncoder encoder) {
        super(userRepository, userConverter);
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.userConverter = userConverter;
        this.encoder = encoder;
    }

    @Override
    public UserDTO save(UserDTO userDTO) {
        return Optional.ofNullable(userDTO)
                .map(userDTO1 -> {
                    String password = userDTO1.getPassword();
                    userDTO1.setPassword(encrypt(password));
                    return userDTO1;
                })
                .map(super::save)
                .orElseThrow(() -> new NullPointerException("Unable to save user"));
    }

    private String encrypt(String string) {
        if (Strings.isBlank(string)) {
            throw new NullPointerException("Password is empty");
        }
        return encoder.encode(string);
    }

    public boolean existByLogin(String userLogin) {
        return userRepository.existsByLogin(userLogin);
    }

    public Optional<UserDTO> findByLogin(String name) {
        return userRepository.findUserByLogin(name)
                .map(userConverter::toDTO);
    }

    public UserDTO updateGroup(Long userId, Long groupId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NullPointerException("No such entity"));
        Group group = Optional.ofNullable(groupId)
                .flatMap(groupRepository::findById)
                .orElse(null);
        user.setGroup(group);
        User savedUser = userRepository.save(user);
        return userConverter.toDTO(savedUser);
    }

    public Page<UserDTO> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable)
                .map(userConverter::toDTO);
    }
}