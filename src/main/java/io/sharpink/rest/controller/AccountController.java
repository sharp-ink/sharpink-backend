package io.sharpink.rest.controller;

import io.sharpink.rest.dto.response.user.UserResponse;
import io.sharpink.rest.exception.Unauthorized401Exception;
import io.sharpink.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/accounts")
public class AccountController {

	private AccountService accountService;

	@Autowired
	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	@RequestMapping(value = "log-in", method = { RequestMethod.GET, RequestMethod.HEAD })
	public UserResponse logIn(@RequestParam String login, @RequestParam String password) {

		Optional<UserResponse> authenticatedUser = accountService.logIn(login, password);

		if (authenticatedUser.isPresent()) {
			return authenticatedUser.get();
		} else {
			throw new Unauthorized401Exception();
		}

	}

}
