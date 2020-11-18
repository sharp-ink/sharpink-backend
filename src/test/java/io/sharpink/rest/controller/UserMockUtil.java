package io.sharpink.rest.controller;

import io.sharpink.rest.dto.response.user.UserResponse;

public class UserMockUtil {

  static final UserResponse USER_RESPONSE_MOCK = mockUserResponse();

  private static UserResponse mockUserResponse() {
    return UserResponse.builder()
      .id(1L)
      .nickname("Batman")
      .email("dark-knight@gotham.com")
      .build();
  }

}
