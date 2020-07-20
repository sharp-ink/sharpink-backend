package io.sharpink.rest.controller;

import java.util.Optional;
import io.sharpink.rest.dto.response.member.MemberResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.sharpink.rest.exception.Unauthorized401Exception;
import io.sharpink.service.AccountService;

@RestController
@RequestMapping("/accounts")
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 86400)
public class AccountController {

	private AccountService accountService;

	@Autowired
	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	@RequestMapping(value = "log-in", method = { RequestMethod.GET, RequestMethod.HEAD })
	public MemberResponse logIn(@RequestParam String login, @RequestParam String password) {

		Optional<MemberResponse> authenticateMember = accountService.logIn(login, password);

		if (authenticateMember.isPresent()) {
			return authenticateMember.get();
		} else {
			throw new Unauthorized401Exception();
		}

	}

}
