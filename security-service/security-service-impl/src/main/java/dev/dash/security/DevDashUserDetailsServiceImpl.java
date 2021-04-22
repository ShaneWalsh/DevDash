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
import dev.dash.model.SecurityRole;
import dev.dash.model.SecurityUser;
import dev.dash.security.util.JwtUtil;
import dev.dash.security.util.SecurityAuthority;

@Transactional
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
            roles.add( new SecurityAuthority(role.getCode()));
        }
        User user = new User(securityUser.getUsername(), securityUser.getPassword(), roles);
        return user;
    }
    
}
