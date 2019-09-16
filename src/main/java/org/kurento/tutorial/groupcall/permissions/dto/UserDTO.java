package org.kurento.tutorial.groupcall.permissions.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@Builder
public class UserDTO implements EntityDTO<Long> {
    private Long id;
    @Pattern(regexp = "^(?=.*[A-Za-z0-9]$)[A-Za-z][A-Za-z\\d.-]{0,256}$")
    private String login;
    @Pattern(regexp = "^(?=.*[A-Za-z0-9]$)[A-Za-z][A-Za-z\\d.-]{0,256}$")
    private String password;
    private String groupName;
    private List<String> permissions;
}