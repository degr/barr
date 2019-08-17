package com.scnsoft.permissions.controller;

import com.scnsoft.permissions.dto.UserDTO;
import com.scnsoft.permissions.service.AuthenticationService;
import com.scnsoft.permissions.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("users")
public class UserController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    public UserController(UserService userService,
                          AuthenticationService authenticationService) {
        this.userService = userService;

        this.authenticationService = authenticationService;
    }

    @PostMapping("signIn")
    public ResponseEntity signIn(@RequestBody UserDTO user) {
        Map<Object, Object> map = authenticationService.signIn(user);
        return ResponseEntity.ok(map);
    }

    @GetMapping("{id}")
    public UserDTO getByName(@PathVariable(value = "id") String login) {
        return userService.findByLogin(login).orElseThrow(RuntimeException::new);
    }

    @PostMapping("signUp")
    public ResponseEntity signUp(@RequestBody UserDTO user) {
        Map<Object, Object> map = authenticationService.signUp(user);
        return ResponseEntity.ok(map);
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("delete/{id}")
    public void deleteById(@PathVariable(value = "id") Long id) {
        userService.deleteById(id);
    }

    @PreAuthorize("hasAnyAuthority('admin','moderator')")
    @GetMapping(path = "assignGroup", params = {"login", "groupNames"})
    public UserDTO assignGroup(@RequestParam String login, @RequestParam String groupName) {
        userService.assignGroup(login, groupName);
        return userService.findByLogin(login).orElseThrow(RuntimeException::new);
    }

    @PreAuthorize("hasAnyAuthority('admin','moderator')")
    @GetMapping(path = "assignPermission", params = {"login", "groupNames"})
    public UserDTO assignPermission(@RequestParam String login,
                                    @RequestParam String permissionName,
                                    @RequestParam boolean isEnabled) {
        userService.assignAdditionalPermission(login, permissionName, isEnabled);
        return userService.findByLogin(login).orElseThrow(RuntimeException::new);
    }
}