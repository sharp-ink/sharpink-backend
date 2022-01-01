package io.sharpink.api.authentication;

import io.sharpink.api.resource.user.service.UserMapper;
import io.sharpink.api.resource.user.persistence.UserDao;
import io.sharpink.api.shared.enums.StoriesLoadingStrategy;
import io.sharpink.api.resource.user.persistence.user.User;
import io.sharpink.api.resource.user.dto.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

	private final UserDao userDao;
	private final UserMapper userMapper;

	@Autowired
	public AuthenticationService(UserDao userDao, UserMapper userMapper) {
		this.userDao = userDao;
		this.userMapper = userMapper;
	}

	public Optional<UserResponse> logIn(String login, String password) {

		Optional<User> optionalUser = userDao.findByCredentials(login, password);
		return optionalUser.map(user -> userMapper.toUserResponse(user, StoriesLoadingStrategy.DISABLED));

	}
}
