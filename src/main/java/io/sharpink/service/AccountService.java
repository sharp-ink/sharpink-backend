package io.sharpink.service;

import io.sharpink.mapper.user.UserMapper;
import io.sharpink.persistence.dao.user.UserDao;
import io.sharpink.persistence.entity.story.StoriesLoadingStrategy;
import io.sharpink.persistence.entity.user.User;
import io.sharpink.rest.dto.response.user.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
      return Optional.of(userMapper.toUserResponse(optionalUser.get(), StoriesLoadingStrategy.DISABLED));
    } else {
			return Optional.empty();
		}

	}
}
