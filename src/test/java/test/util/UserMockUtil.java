package test.util;

import io.sharpink.api.resource.user.dto.UserResponse;
import io.sharpink.api.resource.user.dto.preferences.AppearanceDto;
import io.sharpink.api.resource.user.dto.preferences.UserPreferencesDto;
import io.sharpink.api.resource.user.persistence.user.User;
import io.sharpink.api.resource.user.persistence.user.UserPreferences;
import io.sharpink.api.resource.user.persistence.user.preferences.Theme;
import io.sharpink.util.JsonUtil;

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
