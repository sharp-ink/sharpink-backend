package io.sharpink.service;

import io.sharpink.mapper.story.StoryMapper;
import io.sharpink.mapper.user.UserMapper;
import io.sharpink.persistence.dao.user.UserDao;
import io.sharpink.persistence.entity.story.ChaptersLoadingStrategy;
import io.sharpink.persistence.entity.story.StoriesLoadingStrategy;
import io.sharpink.persistence.entity.story.Story;
import io.sharpink.persistence.entity.user.User;
import io.sharpink.persistence.entity.user.UserDetails;
import io.sharpink.persistence.entity.user.UserPreferences;
import io.sharpink.rest.dto.request.user.UserPatchRequest;
import io.sharpink.rest.dto.response.story.StoryResponse;
import io.sharpink.rest.dto.response.user.UserResponse;
import io.sharpink.rest.dto.shared.user.preferences.UserPreferencesDto;
import io.sharpink.rest.exception.InternalError500Exception;
import io.sharpink.rest.exception.MissingEntity;
import io.sharpink.rest.exception.NotFound404Exception;
import io.sharpink.service.picture.PictureManagementService;
import io.sharpink.util.json.JsonUtil;
import io.sharpink.util.picture.PictureUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static io.sharpink.constant.Constants.USERS_PROFILE_PICTURES_PATH;
import static io.sharpink.constant.Constants.USERS_PROFILE_PICTURES_WEB_URL;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;

@Service
public class UserService {

  private UserDao userDao;
  private UserMapper userMapper;
  private StoryMapper storyMapper;
  private PictureManagementService pictureManagementService;

  @Autowired
  public UserService(UserDao userDao, UserMapper userMapper, StoryMapper storyMapper, PictureManagementService pictureManagementService) {
    this.userDao = userDao;
    this.userMapper = userMapper;
    this.storyMapper = storyMapper;
    this.pictureManagementService = pictureManagementService;
  }

  public List<UserResponse> getAllUsers() {
    List<User> users = (List<User>) userDao.findAll();
    return userMapper.map(users, StoriesLoadingStrategy.DISABLED);
  }

  public UserResponse getUser(Long id) {

    Optional<User> userOptional = userDao.findById(id);

    if (userOptional.isPresent()) {
      return userMapper.map(userOptional.get(), StoriesLoadingStrategy.DISABLED);
    } else {
      throw new NotFound404Exception(MissingEntity.USER, "User not found for id=" + id);
    }

  }

  public List<StoryResponse> getStories(Long memberId) {
    Optional<User> userOptional = userDao.findById(memberId);
    if (userOptional.isPresent()) {
      List<Story> stories = userOptional.get().getStories().stream().sorted(reverseOrder()).collect(toList());
      Collections.sort(stories, Collections.reverseOrder());
      return storyMapper.toStoryResponseList(stories, ChaptersLoadingStrategy.NONE);
    } else {
      throw new NotFound404Exception(MissingEntity.USER);
    }
  }

  public UserResponse updateUserProfile(Long id, UserPatchRequest userPatchRequest) {
    User user = userDao.findById(id).orElseThrow(() -> new NotFound404Exception(MissingEntity.USER));

    // update profile informations
    user.setNickname(userPatchRequest.getNickname());
    user.setEmail(userPatchRequest.getEmail());
    UserDetails newUserDetails = userMapper.map(userPatchRequest);

    // update profile picture (if any) and store it on the file system
    if (!userPatchRequest.getProfilePicture().isEmpty()) {
      String nickname = user.getNickname();
      String extension = PictureUtil.extractExtension(userPatchRequest.getProfilePicture());
      String profilePictureWebUrl = USERS_PROFILE_PICTURES_WEB_URL + '/' + nickname + '/' + nickname + '.' + extension;
      newUserDetails.setProfilePicture(profilePictureWebUrl);
      try {
        String profilePictureBase64Content = PictureUtil.extractBase64Content(userPatchRequest.getProfilePicture());
        String profilePictureFSPath = USERS_PROFILE_PICTURES_PATH + '/' + nickname + '/' + nickname + '.' + extension;
        pictureManagementService.storePictureOnFileSystem(profilePictureBase64Content, profilePictureFSPath);
      } catch (IOException e) {
        e.printStackTrace(); // TODO: use a logger instead
        throw new InternalError500Exception(e);
      }
    } else if (user.getUserDetails().isPresent() && user.getUserDetails().get().getProfilePicture() != null) {
      // keep old profile picture (if there was one)
      newUserDetails.setProfilePicture(user.getUserDetails().get().getProfilePicture());
    }

    // update profile in DB
    user.setUserDetails(newUserDetails);
    newUserDetails.setUser(user);
    userDao.save(user);

    return userMapper.map(user, StoriesLoadingStrategy.DISABLED);
  }

  public UserPreferencesDto getPreferences(Long id) {
    User user = userDao.findById(id).orElseThrow(() -> new NotFound404Exception(MissingEntity.USER));
    if (user.getUserPreferences().isPresent()) {
      return userMapper.map(user.getUserPreferences().get());
    } else {
      return new UserPreferencesDto();
    }
  }

  public UserPreferencesDto updateUserPreferences(Long id, UserPreferencesDto userPreferencesDto) {
    User user = userDao.findById(id).orElseThrow(() -> new NotFound404Exception(MissingEntity.USER));
    UserPreferences userPreferences = user.getUserPreferences().orElseGet(UserPreferences::new);
    UserPreferencesDto currentUserPreferencesDto = StringUtils.isEmpty(userPreferences.getPreferences()) ?
      new UserPreferencesDto() :
      JsonUtil.fromJson(userPreferences.getPreferences(), UserPreferencesDto.class);

    if (userPreferencesDto.getAppearance() != null && userPreferencesDto.getAppearance().getTheme() != null) {
      currentUserPreferencesDto.getAppearance().setTheme(userPreferencesDto.getAppearance().getTheme());
    }

    userPreferences.setPreferences(JsonUtil.toJson(currentUserPreferencesDto));
    userDao.save(user);
    return userMapper.map(userPreferences);
  }
}
