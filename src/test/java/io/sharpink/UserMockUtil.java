package io.sharpink;

import io.sharpink.persistence.entity.user.User;
import io.sharpink.persistence.entity.user.UserPreferences;
import io.sharpink.persistence.entity.user.preferences.Theme;
import io.sharpink.rest.dto.response.user.UserResponse;
import io.sharpink.rest.dto.shared.user.preferences.AppearanceDto;
import io.sharpink.rest.dto.shared.user.preferences.UserPreferencesDto;
import io.sharpink.util.json.JsonUtil;

public class UserMockUtil {

  public static final User USER_MOCK = mockUser();
  public static final UserPreferences USER_PREFERENCES_MOCK = mockUserPreferences();
  public static final UserResponse USER_RESPONSE_MOCK = mockUserResponse();

  private static User mockUser() {
    return User.builder().id(1L).nickname("Batman").email("dark-knight@gotham.com")
      // TODO set more fields if needed
      .build();
  }

  private static UserPreferences mockUserPreferences() {
    return UserPreferences.builder()
      .user(USER_MOCK)
      .preferences(JsonUtil.toJson(UserPreferencesDto.builder()
        .appearance(AppearanceDto.builder().theme(Theme.CANDY).build())
        .build()))
      .build();
  }

  private static UserResponse mockUserResponse() {
    return UserResponse.builder()
      .id(1L)
      .nickname("Batman")
      .email("dark-knight@gotham.com")
      // TODO set more fields if needed
      .build();
  }

}
