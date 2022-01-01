package io.sharpink.api.authentication;

import io.sharpink.api.resource.user.dto.UserResponse;
import io.sharpink.api.shared.exception.Unauthorized401Exception;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

	private final AuthenticationService authenticationService;

	@Autowired
	public AuthenticationController(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@GetMapping("log-in")
	public UserResponse logIn(@RequestParam String login, @RequestParam String password) {
		Optional<UserResponse> authenticatedUser = authenticationService.logIn(login, password);
		return authenticatedUser.orElseThrow(Unauthorized401Exception::new);
	}

}
