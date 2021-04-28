package dev.dash.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface DevDashUserDetailsService extends UserDetailsService  {
    
    /**
     * Gets the security user with the username and sets their security roles as granted Authority
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    /**
     * Generates a JWT with the user details for the username supplied.
     */
    public String generateJWT(String username) throws UsernameNotFoundException;
    
}
