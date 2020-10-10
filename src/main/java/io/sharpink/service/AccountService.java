package io.sharpink.service;

import java.util.Optional;

import io.sharpink.mapper.user.UserMapper;
import io.sharpink.persistence.entity.story.StoriesLoadingStrategy;
import io.sharpink.rest.dto.response.user.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.sharpink.persistence.dao.UserDao;
import io.sharpink.persistence.entity.user.User;

@Service
public class AccountService {

	private UserDao userDao;
	private UserMapper userMapper;

	@Autowired
	public AccountService(UserDao userDao, UserMapper userMapper) {
		this.userDao = userDao;
		this.userMapper = userMapper;
	}

	public Optional<UserResponse> logIn(String login, String password) {

		Optional<User> optionalUser = userDao.findByCredentials(login, password);
		if (optionalUser.isPresent()) {
			return Optional.of(userMapper.map(optionalUser.get(), StoriesLoadingStrategy.DISABLED));
		} else {
			return Optional.empty();
		}

	}
}
