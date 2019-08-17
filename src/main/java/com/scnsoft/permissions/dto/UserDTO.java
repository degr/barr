package com.scnsoft.permissions.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class UserDTO implements EntityDTO {
    private Long id;
    private String login;
    private String password;
    private String groupName;
    private Map<String, Boolean> additionalPermissions;
}