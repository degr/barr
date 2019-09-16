package org.kurento.tutorial.groupcall.permissions.controller;

import lombok.RequiredArgsConstructor;
import org.kurento.tutorial.groupcall.permissions.dto.GroupDTO;
import org.kurento.tutorial.groupcall.permissions.dto.UserDTO;
import org.kurento.tutorial.groupcall.permissions.service.PermissionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("permissions")
@PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @GetMapping(path = "/user/assign")
    public UserDTO assignUserPermission(@RequestParam Long id, @RequestParam Long permId, @RequestParam boolean enabled) {
        return permissionService.assignPermissionToUser(id, permId, enabled);
    }

    @GetMapping(path = "/user/release")
    public UserDTO releaseUserPermission(@RequestParam Long id, @RequestParam Long permId) {
        return permissionService.releasePermissionFromUser(id, permId);
    }

    @GetMapping(path = "/group/assign")
    public GroupDTO assignGroupPermission(@RequestParam Long id, @RequestParam Long permId) {
        return permissionService.assignPermissionToGroup(id, permId);
    }

    @GetMapping(path = "/group/release")
    public GroupDTO releaseGroupPermission(@RequestParam Long id, @RequestParam Long permId) {
        return permissionService.releasePermissionFromGroup(id, permId);
    }
}