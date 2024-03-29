package dev.dash.security;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.dash.dao.SecurityRoleRepository;
import dev.dash.dao.SecurityUserRepository;
import dev.dash.enums.AdminDefaultRolesEnum;
import dev.dash.enums.AuditEventTypeEnum;
import dev.dash.enums.UserTypeEnum;
import dev.dash.model.SecurityRole;
import dev.dash.model.SecurityUser;
import dev.dash.model.dto.admin.SecurityRoleDTO;
import dev.dash.model.dto.admin.SecurityUserDTO;

@Transactional
@Service
public class AdminLogicServiceImpl implements AdminLogicService {

    @Autowired
    private SecurityUserRepository securityUserRepository;
    
    @Autowired
    private SecurityRoleRepository securityRoleRepository;

    @Autowired
    private AuditLogicService auditLogicService;

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
                securityUser.getSecurityRolesSet() == null ? Collections.emptyList() : 
                    securityUser.getSecurityRolesSet().stream().map(role -> role.getCode()).collect(Collectors.toList())
            )
        ).collect(Collectors.toList());

        String ids = "getSecurityUserList:"+ dtos.stream().map(dto -> dto.getUsername()).reduce("", (a,b) -> (a.length()>0)? a+","+b : a+b);
        auditLogicService.auditEntityEvent(ids, AuditEventTypeEnum.EntitySelect);
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
                addSecurityUser.getSecurityRolesSet().stream().map(roleCode -> securityRoleRepository.findByCode(roleCode)).filter(value -> value != null).collect(Collectors.toSet());
        securityUser.setSecurityRolesSet( rolesSet );
        SecurityUser saveAndFlushedUser = this.securityUserRepository.saveAndFlush(securityUser);
        auditLogicService.auditEntityEvent(saveAndFlushedUser, AuditEventTypeEnum.EntityInsert, addSecurityUser);
        return saveAndFlushedUser.getId();
    }

    @Override
    public boolean updateSecurityUser(SecurityUserDTO updateSecurityUser) {
        Optional<SecurityUser> securityUserOpt = securityUserRepository.findById(updateSecurityUser.getId());
        if (securityUserOpt != null && securityUserOpt.isPresent()){
            SecurityUser securityUser = securityUserOpt.get();
            auditLogicService.auditEntityEvent(securityUser, AuditEventTypeEnum.EntityUpdate, updateSecurityUser);
            securityUser.setUsername(updateSecurityUser.getUsername());
            securityUser.setUserType(updateSecurityUser.getUserType());
            securityUser.setDisabledUser(updateSecurityUser.isDisabledUser());
            Set<SecurityRole> rolesSet = (updateSecurityUser.getSecurityRolesSet() == null || updateSecurityUser.getSecurityRolesSet().size() == 0) ? 
                Collections.emptySet() :
                updateSecurityUser.getSecurityRolesSet().stream().map(roleCode -> securityRoleRepository.findByCode(roleCode)).filter(value -> value != null).collect(Collectors.toSet());
            securityUser.setSecurityRolesSet(rolesSet);
            this.securityUserRepository.saveAndFlush(securityUser);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteSecurityUser(Long id) {
        Optional<SecurityUser> securityUser = securityUserRepository.findById(id);
        if (securityUser != null && securityUser.isPresent()) {
            SecurityUser entity = securityUser.get();
            auditLogicService.auditEntityEvent(new AuditableInstance(id,"SecurityUser"), AuditEventTypeEnum.EntityDelete);
            this.securityUserRepository.delete(entity);
            this.securityUserRepository.flush();
            return true;
        }
        return false;
    }

    @Override
    public boolean resetSecurityUserPassword( String username, String password ) {
        auditLogicService.auditEntityEvent("resetSecurityUserPassword:"+username, AuditEventTypeEnum.EntityUpdate);
        SecurityUser securityUser = securityUserRepository.findByUsername(username);
        if (securityUser != null){
            securityUser.setPassword(passwordEncoder.encode(password));
            this.securityUserRepository.saveAndFlush(securityUser);
            return true;
        }
        return false;
    }

    @Override
    public List<SecurityRoleDTO> getSecurityRoleList() {
        List<SecurityRole> roles = securityRoleRepository.findAll();
        List<SecurityRoleDTO> roleDTOs = roles.stream().map(role -> new SecurityRoleDTO (
            role.getId(),
            role.getCode(),
            role.getDescription(),
            (role.getParentSecurityRole() != null)?role.getParentSecurityRole().getCode():null,
            (role.getChildren() != null && role.getChildren().size() > 0)?role.getChildren().stream().map(child -> child.getCode()).collect(Collectors.toList()):null
        )).collect(Collectors.toList());
        String ids = "getSecurityRoleList:"+ roleDTOs.stream().map(roleDto -> roleDto.getCode()).reduce("", (a,b) -> (a.length()>0)? a+","+b : a+b);
        auditLogicService.auditEntityEvent(ids, AuditEventTypeEnum.EntitySelect);
        return roleDTOs;
    }

    @Override
    public Long addSecurityRole(SecurityRoleDTO addSecurityRole) {
        // by default the admin role will be assigned to the top Job if none is defined, thats just how it is :D
        SecurityRole parentSecRole = (addSecurityRole.getParentSecurityRole() != null)? 
            securityRoleRepository.findByCode(addSecurityRole.getParentSecurityRole()) : 
            securityRoleRepository.findByCode(AdminDefaultRolesEnum.DD_CONFIGURATOR_DASHBOARD.getSecurityRoleCode());
        SecurityRole securityRole = new SecurityRole (
            addSecurityRole.getCode(),
            addSecurityRole.getDescription(),
            parentSecRole);
        // new Role cannot have any children
        // if(addSecurityRole.getChildren() != null && addSecurityRole.getChildren().size() > 0) {
        //     Set<SecurityRole> childrenRoles = addSecurityRole.getChildren().stream().map(child -> securityRoleRepository.findByCode(child)).collect(Collectors.toSet());
        //     securityRole.setChildren(childrenRoles);
        // }
        SecurityRole saveAndFlushedRole = this.securityRoleRepository.save(securityRole);
        // update the parent side of the relationship
        if(parentSecRole.getChildren() != null) {
            parentSecRole.getChildren().add(securityRole);
        } else {
            parentSecRole.setChildren(new HashSet<>(Arrays.asList(securityRole)));
        }
        this.securityRoleRepository.saveAndFlush(parentSecRole);
        auditLogicService.auditEntityEvent(saveAndFlushedRole, AuditEventTypeEnum.EntityInsert, addSecurityRole);
        return saveAndFlushedRole.getId();
    }

    @Override
    public boolean updateSecurityRole(SecurityRoleDTO updateSecurityRoleDto) {
        Optional<SecurityRole> existingSecRole = securityRoleRepository.findById(updateSecurityRoleDto.getId());
        if(existingSecRole.isPresent()){
            SecurityRole role = existingSecRole.get();
            auditLogicService.auditEntityEvent(role, AuditEventTypeEnum.EntityUpdate, updateSecurityRoleDto );
            SecurityRole parentSecRole = (updateSecurityRoleDto.getParentSecurityRole() != null)? 
                securityRoleRepository.findByCode(updateSecurityRoleDto.getParentSecurityRole()) : 
                ( !updateSecurityRoleDto.getCode().equalsIgnoreCase(AdminDefaultRolesEnum.DD_CONFIGURATOR_DASHBOARD.getSecurityRoleCode() ))?
                    securityRoleRepository.findByCode(AdminDefaultRolesEnum.DD_CONFIGURATOR_DASHBOARD.getSecurityRoleCode()) :
                    null;
            role.setCode(updateSecurityRoleDto.getCode());
            role.setDescription(updateSecurityRoleDto.getDescription());
            SecurityRole currentParentSecurityRole = role.getParentSecurityRole();
            role.setParentSecurityRole(parentSecRole);
            if ( currentParentSecurityRole != null && currentParentSecurityRole.getId() != parentSecRole.getId() ) {
                // update the other side of the relationship because we have removed a child from its parent
                currentParentSecurityRole.getChildren().remove(role);
                this.securityRoleRepository.save(currentParentSecurityRole);
            }
            // You cannot set children of a role, its managed by adding new roles and setting the paretn role.
            // if(updateSecurityRoleDto.getChildren() != null && updateSecurityRoleDto.getChildren().size() > 0) {
            //     Set<SecurityRole> childrenRoles = updateSecurityRoleDto.getChildren().stream().map(child -> securityRoleRepository.findByCode(child)).collect(Collectors.toSet());
            //     role.setChildren(childrenRoles);
            // }
            SecurityRole saveAndFlushedRole = this.securityRoleRepository.saveAndFlush(role);
            return saveAndFlushedRole != null;
        }
        return false;
    }

    @Override
    public boolean deleteSecurityRole(Long id) {
        Optional<SecurityRole> existingSecRole = securityRoleRepository.findById(id);
        if(existingSecRole.isPresent()) {
            SecurityRole role = existingSecRole.get();
            role.getParentSecurityRole().getChildren().remove(role);
            Set<SecurityUser> updatedSecurityUsers = new HashSet<>();

            for( SecurityRole child: role.getChildren()) {
                role.getChildren().remove(child);
                deleteChildRole(child,updatedSecurityUsers);
            }

            // save changes to security user relationships
            for(SecurityUser securityUser: updatedSecurityUsers) {
                securityUserRepository.save(securityUser);
            }

            auditLogicService.auditEntityEvent(new AuditableInstance(role.getId(),"SecurityRole"), AuditEventTypeEnum.EntityDelete);
            securityRoleRepository.delete(role);
            this.securityUserRepository.flush();
            this.securityRoleRepository.flush();
            return true;
        }
        return false;
    }

    private void deleteChildRole(SecurityRole securityRole, Set<SecurityUser> updatedSecurityUsers) {
        for ( SecurityUser securityUser : securityRole.getSecurityUsersSet() ) {
            securityUser.getSecurityRolesSet().remove(securityRole);
            if ( !updatedSecurityUsers.contains(securityUser) ) updatedSecurityUsers.add(securityUser);
        }
        for(SecurityRole child: securityRole.getChildren()){
            deleteChildRole(child,updatedSecurityUsers);
        }
        auditLogicService.auditEntityEvent(new AuditableInstance(securityRole.getId(),"SecurityRole"), AuditEventTypeEnum.EntityDelete);
        securityRoleRepository.delete(securityRole);
    }


}
