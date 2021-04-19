package dev.dash.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import dev.dash.model.body.LoginRequest;
import dev.dash.model.body.LoginResponse;
import dev.dash.security.DevDashUserDetailsService;

@RestController
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private DevDashUserDetailsService devDashUserDetailsService;

	@RequestMapping(value = "/auth", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) throws Exception {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
			);
		}
		catch (BadCredentialsException e) {
			throw new Exception("Invalid Login", e);
		}

		final String jwt = devDashUserDetailsService.generateJWT(loginRequest.getUsername());

		return ResponseEntity.ok(new LoginResponse(jwt));
	}

}