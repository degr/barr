package org.kurento.tutorial.groupcall.permissions.controller;

import lombok.RequiredArgsConstructor;
import org.kurento.tutorial.groupcall.permissions.dto.UserDTO;
import org.kurento.tutorial.groupcall.permissions.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {
    private static final String TOTAL_PAGES = "totalPages";
    private static final String TOTAL_ELEMENTS = "totalElements";
    private static final String USERS = "users";
    private final UserService userService;

    @GetMapping(value = "{id}")
    public UserDTO getById(@PathVariable(value = "id") Long id) {
        return userService.findById(id).orElseThrow(RuntimeException::new);
    }

    @GetMapping(path = "/assign_group")
    public UserDTO assignGroup(@RequestParam Long id, @RequestParam Long groupId) {
        return userService.updateGroup(id, groupId);
    }

    @GetMapping(path = "/release_group")
    public UserDTO releaseGroup(@RequestParam Long id) {
        return userService.updateGroup(id, null);
    }

    @GetMapping(value = "user")
    public UserDTO getByLogin(@RequestParam String login) {
        return userService.findByLogin(login).orElseThrow(RuntimeException::new);
    }

    @GetMapping()
    public ResponseEntity getAll(@RequestParam int page, @RequestParam int size) {
        Page<UserDTO> all = userService.findAll(page, size);
        return getUsersResponse(all);
    }

    private ResponseEntity getUsersResponse(Page<UserDTO> userPage) {
        int totalPages = userPage.getTotalPages();
        long totalElements = userPage.getTotalElements();
        List<UserDTO> content = userPage.getContent();
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put(TOTAL_PAGES, totalPages);
        responseMap.put(TOTAL_ELEMENTS, totalElements);
        responseMap.put(USERS, content);
        return ResponseEntity.ok(responseMap);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("delete/{id}")
    public void deleteById(@PathVariable(value = "id") Long id) {
        userService.deleteById(id);
    }
}