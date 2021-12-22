package io.sharpink.service;

import io.sharpink.config.SharpinkConfiguration;
import io.sharpink.mapper.story.StoryMapper;
import io.sharpink.mapper.user.UserMapper;
import io.sharpink.persistence.dao.user.UserDao;
import io.sharpink.persistence.entity.story.ChaptersLoadingStrategy;
import io.sharpink.persistence.entity.story.Story;
import io.sharpink.persistence.entity.user.StoriesLoadingStrategy;
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
import java.util.List;
import java.util.Optional;

import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;
import static org.apache.logging.log4j.util.Strings.isNotEmpty;

@Service
public class UserService {

    private final UserDao userDao;
    private final UserMapper userMapper;
    private final StoryMapper storyMapper;
    private final PictureManagementService pictureManagementService;
    private final SharpinkConfiguration sharpinkConfiguration;

    @Autowired
    public UserService(UserDao userDao, UserMapper userMapper, StoryMapper storyMapper, PictureManagementService pictureManagementService,
        SharpinkConfiguration sharpinkConfiguration
    ) {
        this.userDao = userDao;
        this.userMapper = userMapper;
        this.storyMapper = storyMapper;
        this.pictureManagementService = pictureManagementService;
        this.sharpinkConfiguration = sharpinkConfiguration;
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = (List<User>) userDao.findAll();
        return userMapper.toUserResponseList(users, StoriesLoadingStrategy.DISABLED);
    }

    public UserResponse getUser(Long id) {

        Optional<User> userOptional = userDao.findById(id);

        if (userOptional.isPresent()) {
            return userMapper.toUserResponse(userOptional.get(), StoriesLoadingStrategy.DISABLED);
        } else {
            throw new NotFound404Exception(MissingEntity.USER, "User not found for id=" + id);
        }

    }

    public List<StoryResponse> getStories(Long userId) {
        Optional<User> userOptional = userDao.findById(userId);
        if (userOptional.isPresent()) {
            List<Story> stories = userOptional.get().getStories().stream().sorted(reverseOrder()).collect(toList());
            return storyMapper.toStoryResponseList(stories, ChaptersLoadingStrategy.NONE);
        } else {
            throw new NotFound404Exception(MissingEntity.USER, "User not found for id=" + userId);
        }
    }

    public UserResponse updateUserProfile(Long userId, UserPatchRequest userPatchRequest) {
        User user = userDao.findById(userId)
            .orElseThrow(() -> new NotFound404Exception(MissingEntity.USER, "User not found for id=" + userId));

        // update profile informations

        if (isNotEmpty(userPatchRequest.getNickname())) {
            user.setNickname(userPatchRequest.getNickname());
        }

        if (isNotEmpty(userPatchRequest.getEmail())) {
            user.setEmail(userPatchRequest.getEmail());
        }

        UserDetails newUserDetails = userMapper.toUserDetails(userPatchRequest);

        // update profile picture (if any) and store it on the file system
        if (isNotEmpty(userPatchRequest.getProfilePicture())) {
            String nickname = user.getNickname();
            String extension = PictureUtil.extractExtension(userPatchRequest.getProfilePicture());
            String profilePictureWebUrl = sharpinkConfiguration.getUsersProfilePictureWebUrl() + '/' + nickname + '/' + nickname + '.' + extension;
            newUserDetails.setProfilePicture(profilePictureWebUrl);
            try {
                String profilePictureBase64Content = PictureUtil.extractBase64Content(userPatchRequest.getProfilePicture());
                String profilePictureFSPath = sharpinkConfiguration.getUsersProfilePictureFileSystemPath() + '/' + nickname + '/' + nickname + '.' + extension;
                pictureManagementService.storePictureOnFileSystem(profilePictureBase64Content, profilePictureFSPath);
            } catch (IOException e) {
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

        return userMapper.toUserResponse(user, StoriesLoadingStrategy.DISABLED);
    }

    public UserPreferencesDto getPreferences(Long userId) {
        User user = userDao.findById(userId)
            .orElseThrow(() -> new NotFound404Exception(MissingEntity.USER, "User not found for id=" + userId));
        if (user.getUserPreferences().isPresent()) {
            return userMapper.toUserPreferencesDto(user.getUserPreferences().get());
        } else {
            return new UserPreferencesDto();
        }
    }

    public UserPreferencesDto updateUserPreferences(Long userId, UserPreferencesDto userPreferencesDto) {
        User user = userDao.findById(userId)
            .orElseThrow(() -> new NotFound404Exception(MissingEntity.USER, "User not found for id=" + userId));
        UserPreferences userPreferences = user.getUserPreferences().orElseGet(UserPreferences::new);
        UserPreferencesDto currentUserPreferencesDto = StringUtils.isEmpty(userPreferences.getPreferences()) ?
            new UserPreferencesDto() :
            JsonUtil.fromJson(userPreferences.getPreferences(), UserPreferencesDto.class);

        if (userPreferencesDto.getAppearance() != null && userPreferencesDto.getAppearance().getTheme() != null) {
            currentUserPreferencesDto.getAppearance().setTheme(userPreferencesDto.getAppearance().getTheme());
        }

        userPreferences.setPreferences(JsonUtil.toJson(currentUserPreferencesDto));
        userDao.save(user);
        return userMapper.toUserPreferencesDto(userPreferences);
    }
}
