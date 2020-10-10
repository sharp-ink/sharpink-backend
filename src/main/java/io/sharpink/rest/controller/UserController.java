package io.sharpink.rest.controller;

import java.util.List;
import java.util.Optional;

import io.sharpink.rest.dto.request.user.UserPatchRequest;
import io.sharpink.rest.dto.response.user.UserResponse;
import io.sharpink.rest.dto.response.story.StoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.sharpink.rest.exception.NotFound404Exception;
import io.sharpink.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

	private UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("")
	public List<UserResponse> getUsers() {
		return userService.getAllUsers();
	}

	@GetMapping("/{id}")
	public UserResponse getUser(@PathVariable Long id) {
		Optional<UserResponse> optionalUserResponse = userService.getUser(id);
		if (optionalUserResponse.isPresent()) {
			return optionalUserResponse.get();
		} else {
			throw new NotFound404Exception();
		}
	}

	@GetMapping("/{id}/stories")
  public List<StoryResponse> getStories(@PathVariable Long id) {
	  return userService.getStories(id);
  }

	@PutMapping("/{id}/profile")
  public UserResponse updateUserProfile(@PathVariable Long id, @RequestBody UserPatchRequest userPatchRequest) {
	  return userService.updateUserProfile(id, userPatchRequest);
  }
}
