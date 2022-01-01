package io.sharpink.api.resource.user.service;

import io.sharpink.api.resource.story.dto.StoryResponse;
import io.sharpink.api.resource.story.enums.ChaptersLoadingStrategy;
import io.sharpink.api.resource.story.persistence.Story;
import io.sharpink.api.resource.story.service.StoryMapper;
import io.sharpink.api.resource.user.dto.UserPatchRequest;
import io.sharpink.api.resource.user.dto.UserResponse;
import io.sharpink.api.resource.user.dto.preferences.UserPreferencesDto;
import io.sharpink.api.resource.user.persistence.UserDao;
import io.sharpink.api.resource.user.persistence.user.User;
import io.sharpink.api.resource.user.persistence.user.UserDetails;
import io.sharpink.api.resource.user.persistence.user.UserPreferences;
import io.sharpink.api.shared.enums.StoriesLoadingStrategy;
import io.sharpink.api.shared.exception.InternalError500Exception;
import io.sharpink.api.shared.exception.MissingEntity;
import io.sharpink.api.shared.exception.NotFound404Exception;
import io.sharpink.api.shared.service.picture.PictureManagementService;
import io.sharpink.config.SharpinkConfiguration;
import io.sharpink.util.PictureUtil;
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
    public UserService(UserDao userDao, UserMapper userMapper, StoryMapper storyMapper,
        PictureManagementService pictureManagementService, SharpinkConfiguration sharpinkConfiguration
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

    public UserResponse updateUser(Long userId, UserPatchRequest userPatchRequest) {
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
        User user = userDao.findById(userId).orElseThrow(() -> new NotFound404Exception(MissingEntity.USER, "User not found for id=" + userId));
        UserPreferences userPreferences = userMapper.toUserPreferences(userPreferencesDto);
        userPreferences.setUser(user);
        user.setUserPreferences(userPreferences);
        userDao.save(user);
        return userMapper.toUserPreferencesDto(userPreferences);
    }
}
