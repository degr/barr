package com.scnsoft.permissions.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GroupDTO implements EntityDTO{
    private Long id;
    private String name;
    private List<String> userNames;
    private List<String> permissionNames;
}