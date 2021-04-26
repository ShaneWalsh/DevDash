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

import dev.dash.enums.AuditEventTypeEnum;
import dev.dash.model.body.LoginRequest;
import dev.dash.model.body.LoginResponse;
import dev.dash.security.AuditLogicService;
import dev.dash.security.DevDashUserDetailsService;
import lombok.AllArgsConstructor;
import lombok.Data;

@RestController
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private DevDashUserDetailsService devDashUserDetailsService;

	@Autowired
	private AuditLogicService auditLogicService;

	@RequestMapping(value = "/auth", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) throws Exception {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
			);
		}
		catch (BadCredentialsException e) {
			auditLogicService.auditEntityEvent(new AuditUsername(loginRequest.getUsername()), AuditEventTypeEnum.LoginFailed);
			throw new Exception("Invalid Login", e);
		}

		final String jwt = devDashUserDetailsService.generateJWT(loginRequest.getUsername());
		auditLogicService.auditEntityEvent(new AuditUsername(loginRequest.getUsername()), AuditEventTypeEnum.LoginSuccess);

		return ResponseEntity.ok(new LoginResponse(jwt));
	}

	/**
	 * The Audit logic needs a POJO for storing the data as JSON
	 */
	@Data
	@AllArgsConstructor
	class AuditUsername {
		String username;
	}
}