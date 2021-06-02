package dev.dash.model.dto.admin;

import java.util.List;

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
    List<String> securityRolesSet;
}
