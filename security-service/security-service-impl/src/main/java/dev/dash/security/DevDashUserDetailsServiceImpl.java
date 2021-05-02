package dev.dash.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dev.dash.dao.SecurityUserRepository;
import dev.dash.enums.UserTypeEnum;
import dev.dash.model.SecurityRole;
import dev.dash.model.SecurityUser;
import dev.dash.security.util.JwtUtil;
import dev.dash.security.util.SecurityAuthority;
import dev.dash.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Slf4j
@Service
public class DevDashUserDetailsServiceImpl implements DevDashUserDetailsService {

    @Autowired
    SecurityUserRepository securityUserRepository;

	@Autowired
	private JwtUtil jwtTokenUtil;
    
    @Override
    public String generateJWT(String username) throws UsernameNotFoundException {
        SecurityUser securityUser = securityUserRepository.findByUsername(username);
        UserDetails user = loadUserByUsername(securityUser.getUsername());

        final UserDetails userDetails = user;

		final String jwt = jwtTokenUtil.generateToken(userDetails);
        return jwt;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SecurityUser securityUser = securityUserRepository.findByUsername(username);
        List<GrantedAuthority> roles = new ArrayList<>();
        Set<SecurityRole> sets = securityUser.getSecurityRolesSet();
        for(SecurityRole role : sets){
            if ( UserTypeEnum.isNotARole(role.getCode()) ) {
                roles.add( new SecurityAuthority( role.getCode() ) );
            } else {
                log.error("User has a security role that matches a user type. This is not allowed and maybe an attempt to circumvent security: {}", role.getCode());
            }
        }
        // Adding the user type role. Has a ROLE_ prefix
        if ( StringUtil.isVaildString(securityUser.getUserType()) ) 
            roles.add( new SecurityAuthority( UserTypeEnum.valueOf(securityUser.getUserType()).getRoleWithPrefix() ) );
        User user = new User(securityUser.getUsername(), securityUser.getPassword(), roles);
        return user;
    }
    
}
