package io.sharpink.api.resource.user.service;

import io.sharpink.api.resource.story.enums.ChaptersLoadingStrategy;
import io.sharpink.api.resource.story.service.StoryMapper;
import io.sharpink.api.resource.user.dto.UserDetailsResponse;
import io.sharpink.api.resource.user.dto.UserPatchRequest;
import io.sharpink.api.resource.user.dto.UserResponse;
import io.sharpink.api.resource.user.dto.preferences.UserPreferencesDto;
import io.sharpink.api.resource.user.persistence.user.User;
import io.sharpink.api.resource.user.persistence.user.UserDetails;
import io.sharpink.api.resource.user.persistence.user.UserPreferences;
import io.sharpink.api.shared.enums.StoriesLoadingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static io.sharpink.util.JsonUtil.fromJson;
import static io.sharpink.util.JsonUtil.toJson;

@Component
public class UserMapper {

    private final StoryMapper storyMapper;

    @Autowired
    public UserMapper(StoryMapper storyMapper) {
        this.storyMapper = storyMapper;
    }

    /**
     * Build a {@code UserResponse} from a {@code User}.
     *
     * @param storiesLoadingStrategy Indicates if stories should be greedily loaded (by default Hibernate loads them lazily)
     */
    public UserResponse toUserResponse(User source, StoriesLoadingStrategy storiesLoadingStrategy) {
        UserResponse target = UserResponse.builder()
            .id(source.getId())
            .nickname(source.getNickname())
            .email(source.getEmail())
            .storiesCount(source.getStoriesCount())
            .registrationDate(source.getRegistrationDate())
            .userDetails(source.getUserDetails().isPresent() ? toUserDetailsResponse(source.getUserDetails().get()) : null)
            .userPreferences(source.getUserPreferences().isPresent() ? toUserPreferencesDto(source.getUserPreferences().get()) : null)
            .build();

        // we load stories only if requested
        if (storiesLoadingStrategy == StoriesLoadingStrategy.ENABLED) {
            // even when we want stories, we don't load chapters
            target.setStories(storyMapper.toStoryResponseList(source.getStories(), ChaptersLoadingStrategy.NONE));
        }

        return target;
    }

    /**
     * Construit une {@code List<UserResponse>} à partir d'une {@code List<User>}.
     *
     * @param source                 La liste des {@code User} à partir duquel on
     *                               construit notre liste de {@code UserResponse}.
     * @param storiesLoadingStrategy Indique s'il faut ou non charger la liste des
     *                               histoires pour chaque membre, qui sont pour rappel
     *                               chargées par Hibernate avec
     *                               l'annotation @LazyCollection(LazyCollectionOption.TRUE),
     *                               donc seulement quand on y accède réellement. <br/>
     *                               true : charge les histoires, false : ne charge pas
     *                               les histoires (on se contente du nombre total
     *                               d'histoires qui, lui, est toujours chargé)
     * @return Une {@code List<UserResponse>}
     */
    public List<UserResponse> toUserResponseList(List<User> source, StoriesLoadingStrategy storiesLoadingStrategy) {
        List<UserResponse> target = new ArrayList<>();
        for (User user : source) {
            target.add(toUserResponse(user, storiesLoadingStrategy));
        }
        return target;
    }

    public UserDetails toUserDetails(UserPatchRequest userPatchRequest) {
        return UserDetails.builder()
            .firstName(userPatchRequest.getFirstName())
            .lastName(userPatchRequest.getLastName())
            // TODO mapper les autres champs
            .build();
    }

    public UserPreferencesDto toUserPreferencesDto(UserPreferences source) {
        return fromJson(source.getPreferences(), UserPreferencesDto.class);
    }

    public UserPreferences toUserPreferences(UserPreferencesDto source) {
        return UserPreferences.builder()
            .preferences(toJson(source))
            .build();
    }

    private UserDetailsResponse toUserDetailsResponse(UserDetails source) {
        return UserDetailsResponse.builder()
            .firstName(source.getFirstName())
            .lastName(source.getLastName())
            .profilePicture(source.getProfilePicture())
            // TODO : finir de mapper les champs de UserDetails
            .build();
    }
}
