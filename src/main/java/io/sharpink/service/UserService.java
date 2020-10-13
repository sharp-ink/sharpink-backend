package io.sharpink.service;

import io.sharpink.mapper.user.UserMapper;
import io.sharpink.persistence.dao.UserDao;
import io.sharpink.persistence.entity.story.StoriesLoadingStrategy;
import io.sharpink.persistence.entity.user.User;
import io.sharpink.persistence.entity.user.UserDetails;
import io.sharpink.persistence.entity.user.UserPreferences;
import io.sharpink.rest.dto.request.user.UserPatchRequest;
import io.sharpink.rest.dto.request.user.UserPreferencesPatchRequest;
import io.sharpink.rest.dto.response.story.StoryResponse;
import io.sharpink.rest.dto.response.user.UserPreferencesResponse;
import io.sharpink.rest.dto.response.user.UserResponse;
import io.sharpink.rest.exception.InternalError500Exception;
import io.sharpink.rest.exception.MissingEntity;
import io.sharpink.rest.exception.NotFound404Exception;
import io.sharpink.service.picture.PictureManagementService;
import io.sharpink.util.picture.PictureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static io.sharpink.constant.Constants.USERS_PROFILE_PICTURES_PATH;
import static io.sharpink.constant.Constants.USERS_PROFILE_PICTURES_WEB_URL;

@Service
public class UserService {

  private UserDao userDao;
  private UserMapper userMapper;
  private StoryService storyService;
  private PictureManagementService pictureManagementService;

  @Autowired
  public UserService(UserDao userDao, UserMapper userMapper, StoryService storyService, PictureManagementService pictureManagementService) {
    this.userDao = userDao;
    this.userMapper = userMapper;
    this.storyService = storyService;
    this.pictureManagementService = pictureManagementService;
  }

  public List<UserResponse> getAllUsers() {
    List<User> users = (List<User>) userDao.findAll();
    return userMapper.map(users, StoriesLoadingStrategy.DISABLED);
  }

  public Optional<UserResponse> getUser(Long id) {

    Optional<User> userOptional = userDao.findById(id);

    if (userOptional.isPresent()) {
      return Optional.of(userMapper.map(userOptional.get(), StoriesLoadingStrategy.DISABLED));
    } else {
      return Optional.empty();
    }

  }

  public List<StoryResponse> getStories(Long memberId) {
    return storyService.getStories(memberId);
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

  public UserPreferencesResponse getPreferences(Long id) {
    User user = userDao.findById(id).orElseThrow(() -> new NotFound404Exception(MissingEntity.USER));
    if (user.getUserPreferences().isPresent()) {
      return userMapper.map(user.getUserPreferences().get());
    } else {
      return new UserPreferencesResponse();
    }
  }

  public UserPreferencesResponse updateUserPreferences(Long id, UserPreferencesPatchRequest userPreferencesPatchRequest) {
    User user = userDao.findById(id).orElseThrow(() -> new NotFound404Exception(MissingEntity.USER));
    UserPreferences userPreferences = user.getUserPreferences().orElseGet(UserPreferences::new);
    if (userPreferencesPatchRequest.getTheme() != null) {
      userPreferences.setTheme(userPreferencesPatchRequest.getTheme());
    }
    user.setUserPreferences(userPreferences);
    userDao.save(user);
    return userMapper.map(userPreferences);
  }
}
