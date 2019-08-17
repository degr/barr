package com.scnsoft.permissions.controller;

import com.scnsoft.permissions.dto.GroupDTO;
import com.scnsoft.permissions.service.GroupService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController(value = "user_permissions")
@RequestMapping("groups")
public class GroupController {
    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public List<GroupDTO> getGroups() {
        return groupService.findAll();
    }

    @GetMapping("{id}")
    public GroupDTO findById(@PathVariable Long id) {
        return groupService.findById(id).orElseThrow(RuntimeException::new);
    }

    @PreAuthorize("hasAnyAuthority('admin','moderator')")
    @GetMapping(path = "assignPermission", params = {"userGroupName", "permissionName"})
    public GroupDTO assignPermission(@RequestParam String userGroupName, @RequestParam String permissionName) {
        return groupService.assignPermission(userGroupName, permissionName).orElseThrow(RuntimeException::new);
    }
    @PreAuthorize("hasAnyAuthority('admin','moderator')")
    @PostMapping()
    public GroupDTO postGroup(@RequestBody GroupDTO groupDTO) {
        return groupService.saveEntity(groupDTO).orElseThrow(RuntimeException::new);
    }
}