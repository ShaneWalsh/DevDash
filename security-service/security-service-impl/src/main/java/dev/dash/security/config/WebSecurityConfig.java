package dev.dash.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import dev.dash.enums.UserTypeEnum;
import dev.dash.security.DevDashUserDetailsService;
import dev.dash.security.filter.JwtRequestFilter;
import dev.dash.security.filter.TaggingFilter;

@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
	private DevDashUserDetailsService devDashUserDetailsService;

    @Autowired
	private JwtRequestFilter jwtRequestFilter;

	@Autowired
	private TaggingFilter taggingFilter;

	@Autowired
	private CorsFilter corsFilter;

	@Value( "${encoder.key.secret}" )
    private String encoderKey;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(devDashUserDetailsService);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder(encoderKey, 10000, 128);
		return encoder;
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf().disable() // crsf is disabled, but cors is enabled by default
				.authorizeRequests()
					.antMatchers("/auth").permitAll() // requests to /auth will be allowed to pass through, no auth required
					// only admins and configs and can import/export
					.antMatchers("/**/importdata").hasAnyRole( UserTypeEnum.Admin.getRole(), UserTypeEnum.Configurator.getRole() ) 
					.antMatchers("/**/exportdata").hasAnyRole( UserTypeEnum.Admin.getRole(), UserTypeEnum.Configurator.getRole() )
					.anyRequest().authenticated().and() 			  // any other request must be authenticated 
					.exceptionHandling().and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
				
				// add my filters before the Auth filter, log the user in if they have a valid session, cors, fish tagging requests
		httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
					.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
					.addFilterBefore(taggingFilter, UsernamePasswordAuthenticationFilter.class);
	}
    
}
