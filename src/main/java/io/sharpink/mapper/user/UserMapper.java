package io.sharpink.mapper.user;

import io.sharpink.mapper.story.StoryMapper;
import io.sharpink.persistence.entity.story.ChaptersLoadingStrategy;
import io.sharpink.persistence.entity.user.StoriesLoadingStrategy;
import io.sharpink.persistence.entity.user.User;
import io.sharpink.persistence.entity.user.UserDetails;
import io.sharpink.persistence.entity.user.UserPreferences;
import io.sharpink.rest.dto.request.user.UserPatchRequest;
import io.sharpink.rest.dto.response.user.UserDetailsResponse;
import io.sharpink.rest.dto.response.user.UserResponse;
import io.sharpink.rest.dto.shared.user.preferences.UserPreferencesDto;
import io.sharpink.util.json.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {

  private final StoryMapper storyMapper;

  @Autowired
  public UserMapper(StoryMapper storyMapper) {
    this.storyMapper = storyMapper;
  }

  /**
   * Construit un objet {@code UserResponse} à partir d'un objet {@code User}.
   *
   * @param source                 L'entité {@code User} à partir de laquelle on
   *                               construit notre objet.
   * @param storiesLoadingStrategy Indique s'il faut ou non charger la liste des
   *                               histoires, qui est pour rappel chargé par Hibernate
   *                               avec
   *                               l'annotation @LazyCollection(LazyCollectionOption.TRUE),
   *                               donc seulement quand on y accède réellement. <br/>
   *                               true : charge les histoires, false : ne charge pas
   *                               les histoires (on se contente du nombre total
   *                               d'histoires qui, lui, est toujours chargé)
   * @return Un {@code User}
   */
  public UserResponse toUserResponse(User source, StoriesLoadingStrategy storiesLoadingStrategy) {
    UserResponse target = UserResponse.builder()
      .id(source.getId())
      .nickname(source.getNickname())
      .email(source.getEmail())
      .storiesCount(source.getStoriesCount())
      .userDetails(source.getUserDetails().isPresent() ? toUserDetailsResponse(source.getUserDetails().get()) : null)
      .build();

    // on ne charge les histoires que si demandé
    if (storiesLoadingStrategy == StoriesLoadingStrategy.ENABLED) {
      // meme si on veut les histoires, on ne veut pas charger les chapitres
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
    if (StringUtils.isEmpty(source.getPreferences())) {
      return new UserPreferencesDto();
    }
    return JsonUtil.fromJson(source.getPreferences(), UserPreferencesDto.class);
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
