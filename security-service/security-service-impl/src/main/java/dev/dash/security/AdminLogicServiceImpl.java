package dev.dash.security;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.dash.dao.SecurityRoleRepository;
import dev.dash.dao.SecurityUserRepository;
import dev.dash.enums.UserTypeEnum;
import dev.dash.model.SecurityRole;
import dev.dash.model.SecurityUser;
import dev.dash.model.dto.admin.SecurityUserDTO;

@Service
public class AdminLogicServiceImpl implements AdminLogicService {

    @Autowired
    private SecurityUserRepository securityUserRepository;
    
    @Autowired
    private SecurityRoleRepository securityRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<SecurityUserDTO> getSecurityUserList() {
        List<SecurityUser> findAll = securityUserRepository.findAll();
        List<SecurityUserDTO> dtos = findAll.stream().map( securityUser -> 
            new SecurityUserDTO(
                securityUser.getId(), 
                securityUser.getUsername(), 
                null,
                securityUser.getUserType(), 
                securityUser.isDisabledUser(), 
                securityUser.getSecurityRolesSet() == null ? Collections.emptySet() : 
                    securityUser.getSecurityRolesSet().stream().map(role -> role.getCode()).collect(Collectors.toSet())
            )
        ).collect(Collectors.toList());
        return dtos;
    }

    @Override
    public Long addSecurityUser(SecurityUserDTO addSecurityUser) {
        SecurityUser securityUser = new SecurityUser( 
            addSecurityUser.getUsername(), 
            passwordEncoder.encode(addSecurityUser.getPassword()),
            UserTypeEnum.valueOf(addSecurityUser.getUserType()).toString() );
        Set<SecurityRole> rolesSet = (addSecurityUser.getSecurityRolesSet() == null || addSecurityUser.getSecurityRolesSet().size() == 0) ? 
                Collections.emptySet() :
                addSecurityUser.getSecurityRolesSet().stream().map(roleCode -> securityRoleRepository.findByCode(roleCode)).collect(Collectors.toSet());
        securityUser.setSecurityRolesSet( rolesSet );
        SecurityUser saveAndFlushedUser = this.securityUserRepository.saveAndFlush(securityUser);
        return saveAndFlushedUser.getId();
    }

}
