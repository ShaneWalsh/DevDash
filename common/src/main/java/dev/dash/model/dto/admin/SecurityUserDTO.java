package dev.dash.model.dto.admin;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SecurityUserDTO {
    Long id;
    String username;
    String password;
    String userType;
    boolean disabledUser;
    Set<String> securityRolesSet;
}
