package dev.dash.model.dto.admin;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SecurityRoleDTO {
    Long id;
    String code;
    String description;
    String parentSecurityRole;
    List<String> children;
}
