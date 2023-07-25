package dev.dash.security;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import dev.dash.model.SecurityRole;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Slf4j
@Service
public class SecurityLogicServiceImpl implements SecurityLogicService {

    @Override
    public boolean checkUserHasRole(SecurityRole requiredRole) { // TODO write a Junit for this
        if( requiredRole == null ){
            return true;
        } else if(checkUserHasRole(requiredRole.getCode())){
            return true;
        } else { // climb the parents and check if the user has any parent roles, keep a set of past roles to prevent infinte loop
            Set<String> checkedRoles = new HashSet<>(); 
            checkedRoles.add(requiredRole.getCode());
            SecurityRole parentRole = requiredRole.getParentSecurityRole();
            while ( parentRole != null && !checkedRoles.contains(parentRole.getCode() ) ) {
                if(checkUserHasRole(parentRole.getCode())) return true;
                checkedRoles.add(parentRole.getCode());
                parentRole = parentRole.getParentSecurityRole();
            }
        }
        // TODO add Audit log for failed check?
        log.warn( "Failed Role check. User:{} role: {}", ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername(), requiredRole.getCode());
        return false;
    }

    @Override
    public boolean checkUserHasRole(String requiredRole) {
        boolean anyMatch = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equalsIgnoreCase(requiredRole));
        log.debug( "User role: {} check has role: {} ", requiredRole, anyMatch );
        return anyMatch;
    }
    
}
