package dev.dash.security;

import javax.transaction.Transactional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import dev.dash.model.SecurityRole;

@Transactional
@Service
public class SecurityLogicServiceImpl implements SecurityLogicService {

    @Override
    public boolean checkUserHasRole(String requiredRole) {
        boolean anyMatch = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equalsIgnoreCase(requiredRole));
        return anyMatch;
    }

    @Override
    public boolean checkUserHasRole(SecurityRole requiredRole) {
        if( requiredRole == null ){
            return true;
        }
        return checkUserHasRole(requiredRole.getCode());
    }
    
}
