package dev.dash.security;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dev.dash.dao.SecurityUserRepository;
import dev.dash.model.SecurityUser;
import dev.dash.security.util.JwtUtil;

@Service
public class DevDashUserDetailsServiceImpl implements DevDashUserDetailsService {

    @Autowired
    SecurityUserRepository securityUserRepository;

	@Autowired
	private JwtUtil jwtTokenUtil;
    
    @Override
    public String generateJWT(String username) throws UsernameNotFoundException {
        SecurityUser securityUser = securityUserRepository.findByUsername(username);
        User user = new User(securityUser.getUsername(), securityUser.getPassword(), new ArrayList<>());

        final UserDetails userDetails = user;

		final String jwt = jwtTokenUtil.generateToken(userDetails);
        return jwt;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SecurityUser securityUser = securityUserRepository.findByUsername(username);
        // todo set roles here for use later by auth mechanism
        User user = new User(securityUser.getUsername(), securityUser.getPassword(), new ArrayList<>());
        return user;
    }
    
}
