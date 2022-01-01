package io.sharpink.api.resource.user.dto;

import io.sharpink.api.resource.story.dto.StoryResponse;
import io.sharpink.api.resource.user.dto.preferences.UserPreferencesDto;
import io.sharpink.api.resource.user.persistence.user.UserPreferences;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    protected Long id;

    // Main information, mandatory
    protected String nickname;
    protected String email;
    protected Long storiesCount;
    protected List<StoryResponse> stories;

    // Complementary information, optional
    protected UserDetailsResponse userDetails;

    // User's preferences, optional
    protected UserPreferencesDto userPreferences;
}
