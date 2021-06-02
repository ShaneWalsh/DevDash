package dev.dash.security;

import java.util.List;

import dev.dash.model.dto.admin.SecurityUserDTO;

public interface AdminLogicService {

    List<SecurityUserDTO> getSecurityUserList();

    Long addSecurityUser( SecurityUserDTO addSecurityUser );
    
    boolean updateSecurityUser( SecurityUserDTO updateSecurityUser );

    boolean deleteSecurityUser( Long id );

    boolean resetSecurityUserPassword( String username, String password );
}
